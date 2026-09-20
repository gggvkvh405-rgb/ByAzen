package org.zenith.base.comand.impl;

import java.util.Locale;

final class CalcCommand_ExpressionParser {
   public final String src;
   public int pos;

   CalcCommand_ExpressionParser(String var1) {
      this.src = var1.toLowerCase(Locale.ROOT)
         .replace(',', '.')
         .replace('x', '*')
         .replace('х', '*')
         .replace('×', '*')
         .replace('÷', '/')
         .replace(':', '/')
         .replace('−', '-');
   }

   double parse() {
      double d0 = this.expression();
      this.skipWs();
      if (this.pos < this.src.length()) {
         throw this.err("Лишний символ '" + this.src.charAt(this.pos) + "'", this.pos);
      } else if (Double.isNaN(d0)) {
         throw new CalcCommand_CalcException("Результат не определён");
      } else if (Double.isInfinite(d0)) {
         throw new CalcCommand_CalcException("Результат слишком большой");
      } else {
         return d0;
      }
   }

   public double expression() {
      double d0 = resolve(this.term());

      while (true) {
         this.skipWs();
         char c0 = this.peek();
         if (c0 != '+' && c0 != '-') {
            return d0;
         }

         this.pos++;
         CalcCommand_Val calccommand_val = this.term();
         double d1 = calccommand_val.pct() ? d0 * calccommand_val.v() / 100.0 : calccommand_val.v();
         d0 = c0 == '+' ? d0 + d1 : d0 - d1;
      }
   }

   public CalcCommand_Val term() {
      CalcCommand_Val calccommand_val = this.unary();

      while (true) {
         this.skipWs();
         char c0 = this.peek();
         if (c0 != '*' && c0 != '/') {
            if (c0 != '(') {
               return calccommand_val;
            }

            calccommand_val = this.multiply(calccommand_val, this.unary(), false);
         } else {
            this.pos++;
            calccommand_val = this.multiply(calccommand_val, this.unary(), c0 == '/');
         }
      }
   }

   public CalcCommand_Val multiply(CalcCommand_Val var1, CalcCommand_Val var2, boolean var3) {
      double d0 = resolve(var1);
      double d1 = resolve(var2);
      if (var3) {
         if (d1 == 0.0) {
            throw new CalcCommand_CalcException("Деление на ноль");
         } else {
            return new CalcCommand_Val(d0 / d1, false);
         }
      } else {
         return new CalcCommand_Val(d0 * d1, false);
      }
   }

   public CalcCommand_Val unary() {
      this.skipWs();
      char c0 = this.peek();
      if (c0 == '+') {
         this.pos++;
         return this.unary();
      } else if (c0 == '-') {
         this.pos++;
         CalcCommand_Val calccommand_val = this.unary();
         return new CalcCommand_Val(-calccommand_val.v(), calccommand_val.pct());
      } else {
         return this.power();
      }
   }

   public CalcCommand_Val power() {
      CalcCommand_Val calccommand_val = this.postfix();
      this.skipWs();
      if (this.peek() != '^') {
         return calccommand_val;
      }

      this.pos++;
      CalcCommand_Val calccommand_val1 = this.unary();
      return new CalcCommand_Val(Math.pow(resolve(calccommand_val), resolve(calccommand_val1)), false);
   }

   public CalcCommand_Val postfix() {
      double d0 = this.primary();
      this.skipWs();
      if (this.peek() == '%') {
         this.pos++;
         return new CalcCommand_Val(d0, true);
      } else {
         return new CalcCommand_Val(d0, false);
      }
   }

   public double primary() {
      this.skipWs();
      if (this.pos >= this.src.length()) {
         throw this.err("Ожидалось число", this.pos);
      }

      char c0 = this.src.charAt(this.pos);
      if (c0 == '(') {
         int i = this.pos++;
         double d0 = this.expression();
         this.skipWs();
         if (this.peek() != ')') {
            throw this.err("Не закрыта скобка", i);
         }

         this.pos++;
         return d0;
      } else if (Character.isDigit(c0) || c0 == '.') {
         return this.number();
      } else if (Character.isLetter(c0)) {
         return this.identifier();
      } else {
         throw this.err("Непонятный символ '" + c0 + "'", this.pos);
      }
   }

   public double number() {
      int i = this.pos;
      boolean flag = false;

      while (this.pos < this.src.length()) {
         char c0 = this.src.charAt(this.pos);
         if (Character.isDigit(c0)) {
            this.pos++;
         } else {
            if (c0 != '.' || flag) {
               break;
            }

            flag = true;
            this.pos++;
         }
      }

      String s = this.src.substring(i, this.pos);
      if (s.equals(".")) {
         throw this.err("Непонятное число", i);
      }

      double d0 = Double.parseDouble(s);
      int j = 0;

      while (this.pos < this.src.length() && (this.src.charAt(this.pos) == 1082 || this.src.charAt(this.pos) == 'k')) {
         j++;
         this.pos++;
      }

      if (j > 4) {
         throw this.err("Слишком длинный суффикс из 'к'", this.pos - j);
      }

      if (j > 0) {
         d0 *= Math.pow(1000.0, j);
      }

      return d0;
   }

   public double identifier() {
      int i = this.pos;

      while (this.pos < this.src.length() && Character.isLetter(this.src.charAt(this.pos))) {
         this.pos++;
      }

      String s = this.src.substring(i, this.pos);
      switch (s) {
         case "pi":
         case "пи":
            return Math.PI;
         case "e":
            return Math.E;
         default:
            this.skipWs();
            if (this.peek() != '(') {
               throw this.err("Неизвестная функция или константа '" + s + "'", i);
            } else {
               int j = this.pos++;
               double d0 = this.expression();
               this.skipWs();
               if (this.peek() != ')') {
                  throw this.err("Не закрыта скобка", j);
               } else {
                  this.pos++;
                  return this.apply(s, d0, i);
               }
            }
      }
   }

   public double apply(String var1, double var2, int var4) {
      return switch (var1) {
         case "sqrt" -> Math.sqrt(var2);
         case "cbrt" -> Math.cbrt(var2);
         case "abs" -> Math.abs(var2);
         case "round" -> Math.round(var2);
         case "floor" -> Math.floor(var2);
         case "ceil" -> Math.ceil(var2);
         case "sin" -> Math.sin(var2);
         case "cos" -> Math.cos(var2);
         case "tan" -> Math.tan(var2);
         case "ln" -> Math.log(var2);
         case "log" -> Math.log10(var2);
         default -> throw this.err("Неизвестная функция '" + var1 + "'", var4);
      };
   }

   public static double resolve(CalcCommand_Val var0) {
      return var0.pct() ? var0.v() / 100.0 : var0.v();
   }

   public char peek() {
      return this.pos < this.src.length() ? this.src.charAt(this.pos) : '\u0000';
   }

   public void skipWs() {
      while (this.pos < this.src.length() && Character.isWhitespace(this.src.charAt(this.pos))) {
         this.pos++;
      }
   }

   public CalcCommand_CalcException err(String var1, int var2) {
      return new CalcCommand_CalcException(var1 + " (позиция " + (var2 + 1) + ")");
   }
}

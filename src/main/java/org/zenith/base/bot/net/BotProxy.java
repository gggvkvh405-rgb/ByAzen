package org.zenith.base.bot.net;

import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import java.net.InetSocketAddress;
import java.net.URI;

public record BotProxy(BotProxy_Type type, String host, int port, String username, String password) {
   public static BotProxy parse(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String s = var0.trim();
         return s.contains("://") ? parseUri(s) : parseLegacy(s);
      } else {
         return null;
      }
   }

   public static BotProxy parseUri(String var0) {
      URI uri;
      try {
         uri = URI.create(var0);
      } catch (IllegalArgumentException illegalargumentexception) {
         throw new IllegalArgumentException("Invalid proxy address", illegalargumentexception);
      }

      BotProxy_Type botproxy_type = BotProxy_Type.fromScheme(uri.getScheme());
      String s = uri.getHost();
      int i = uri.getPort();
      if (s != null && !s.isBlank() && i > 0) {
         BotProxy_Credentials botproxy_credentials = BotProxy_Credentials.parse(uri.getUserInfo());
         return new BotProxy(botproxy_type, s, validatePort(i), botproxy_credentials.username(), botproxy_credentials.password());
      } else {
         throw new IllegalArgumentException("Invalid proxy address");
      }
   }

   public static BotProxy parseLegacy(String var0) {
      String s = var0;
      BotProxy_Credentials botproxy_credentials = BotProxy_Credentials.EMPTY;
      int i = var0.lastIndexOf(64);
      if (i >= 0) {
         botproxy_credentials = BotProxy_Credentials.parse(var0.substring(0, i));
         s = var0.substring(i + 1);
      }

      BotProxy_HostPort botproxy_hostport = BotProxy_HostPort.parse(s);
      if (botproxy_credentials == BotProxy_Credentials.EMPTY && botproxy_hostport.tail().length > 0) {
         botproxy_credentials = BotProxy_Credentials.fromTail(botproxy_hostport.tail());
      }

      return new BotProxy(
         BotProxy_Type.SOCKS5, botproxy_hostport.host(), botproxy_hostport.port(), botproxy_credentials.username(), botproxy_credentials.password()
      );
   }

   public ProxyHandler createHandler() {
      InetSocketAddress inetsocketaddress = new InetSocketAddress(this.host, this.port);
      boolean flag = this.username != null && !this.username.isBlank();
      String s = this.password == null ? "" : this.password;

      return switch (this.type) {
         case SOCKS5 -> flag ? new Socks5ProxyHandler(inetsocketaddress, this.username, s) : new Socks5ProxyHandler(inetsocketaddress);
         case SOCKS4 -> flag ? new Socks4ProxyHandler(inetsocketaddress, this.username) : new Socks4ProxyHandler(inetsocketaddress);
         case HTTP -> flag ? new HttpProxyHandler(inetsocketaddress, this.username, s) : new HttpProxyHandler(inetsocketaddress);
      };
   }

   public static int validatePort(int var0) {
      if (var0 >= 1 && var0 <= 65535) {
         return var0;
      } else {
         throw new IllegalArgumentException("Invalid proxy port");
      }
   }
}

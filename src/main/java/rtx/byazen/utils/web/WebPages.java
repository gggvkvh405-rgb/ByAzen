package rtx.byazen.utils.web;

import java.util.Locale;

/**
 * Страницы веб-сервера клиента (идеи №175 «Мобильный компаньон» и №176 «Веб-дашборд»).
 * <p>
 * Разметка генерируется прямо здесь: ни одной картинки-пикселя, только плавная типографика,
 * скруглённые карточки и аккуратные акценты — та же линия, что и в клиенте. Страницы сами
 * опрашивают {@code /api/state} и {@code /api/stats} и умеют отправлять команды плееру.
 */
public final class WebPages {

    private static final String TOKEN_PLACEHOLDER = "__TOKEN__";

    private WebPages() {
    }

    private static String style() {
        return """
                *{box-sizing:border-box;margin:0;padding:0}
                :root{--bg:#0d1017;--card:#151a24;--card2:#1b2130;--ink:#eaeef7;--sub:#a6aec2;
                      --accent:#7cc4ff;--accent2:#b98bff;--ok:#6cd08a;--warn:#e6ba5c;--bad:#e85c5c}
                body{background:radial-gradient(1200px 600px at 20% -10%,#1b2436 0%,var(--bg) 60%);
                     color:var(--ink);font:15px/1.5 ui-sans-serif,system-ui,"Segoe UI",Inter,Roboto,sans-serif;
                     min-height:100vh;padding:18px 14px 40px;-webkit-font-smoothing:antialiased}
                .wrap{max-width:980px;margin:0 auto;display:flex;flex-direction:column;gap:14px}
                header{display:flex;align-items:center;gap:12px;justify-content:space-between;flex-wrap:wrap}
                .brand{display:flex;align-items:center;gap:12px}
                .logo{width:44px;height:44px;border-radius:14px;background:linear-gradient(135deg,var(--accent),var(--accent2));
                      display:grid;place-items:center;font-weight:700;color:#0b0e14;font-size:18px}
                h1{font-size:20px;font-weight:650;letter-spacing:.2px}
                .muted{color:var(--sub);font-size:13px}
                .pill{padding:6px 12px;border-radius:999px;background:#ffffff12;font-size:13px;color:var(--sub)}
                .pill.on{background:#6cd08a22;color:var(--ok)}
                .pill.off{background:#e85c5c22;color:var(--bad)}
                .grid{display:grid;gap:14px;grid-template-columns:repeat(auto-fit,minmax(220px,1fr))}
                .card{background:linear-gradient(180deg,var(--card),var(--card2));border:1px solid #ffffff12;
                      border-radius:18px;padding:16px;box-shadow:0 10px 30px #00000055}
                .card h2{font-size:13px;text-transform:uppercase;letter-spacing:.9px;color:var(--sub);font-weight:600;margin-bottom:10px}
                .big{font-size:30px;font-weight:680;letter-spacing:-.4px}
                .row{display:flex;justify-content:space-between;gap:10px;padding:5px 0;border-bottom:1px dashed #ffffff10}
                .row:last-child{border-bottom:none}
                .row span:last-child{color:var(--ink);font-variant-numeric:tabular-nums}
                .row span:first-child{color:var(--sub)}
                .bar{height:8px;border-radius:99px;background:#ffffff14;overflow:hidden;margin-top:8px}
                .bar>i{display:block;height:100%;background:linear-gradient(90deg,var(--accent),var(--accent2))}
                button{border:none;border-radius:12px;padding:10px 14px;font:inherit;font-weight:600;cursor:pointer;
                       background:#ffffff12;color:var(--ink);transition:transform .12s ease,background .12s ease}
                button:hover{background:#ffffff1f}
                button:active{transform:scale(.97)}
                button.primary{background:linear-gradient(135deg,var(--accent),var(--accent2));color:#0b0e14}
                .controls{display:flex;gap:8px;flex-wrap:wrap;margin-top:12px}
                canvas{width:100%;height:140px;border-radius:12px;background:#0e131c;border:1px solid #ffffff10}
                .feed{display:flex;flex-direction:column;gap:8px;max-height:260px;overflow:auto}
                .item{display:flex;gap:10px;align-items:flex-start;font-size:14px}
                .item .time{color:var(--sub);font-size:12px;min-width:52px;font-variant-numeric:tabular-nums}
                .tag{font-size:11px;padding:2px 8px;border-radius:99px;background:#ffffff12;color:var(--sub)}
                table{width:100%;border-collapse:collapse;font-size:14px}
                th,td{text-align:left;padding:7px 6px;border-bottom:1px solid #ffffff10}
                th{color:var(--sub);font-weight:600;font-size:12px;text-transform:uppercase;letter-spacing:.6px}
                input{width:100%;padding:10px 12px;border-radius:12px;border:1px solid #ffffff16;background:#0e131c;
                      color:var(--ink);font:inherit}
                footer{color:var(--sub);font-size:12px;text-align:center;padding-top:6px}
                a{color:var(--accent);text-decoration:none}
                @media(max-width:520px){.big{font-size:26px}body{padding:14px 10px 30px}}
                """;
    }

    private static String script() {
        return """
                const TOKEN='__TOKEN__';
                const q=(p)=>p+(p.includes('?')?'&':'?')+'t='+TOKEN;
                const api=(p)=>fetch(q(p)).then(r=>r.json());
                const fmtMs=(ms)=>{const s=Math.floor(ms/1000);const h=Math.floor(s/3600);const m=Math.floor((s%3600)/60);
                  return (h>0?h+' ч ':'')+m+' мин '+String(s%60).padStart(2,'0')+' с'};
                const fmtTime=(t)=>new Date(t).toLocaleTimeString('ru-RU',{hour:'2-digit',minute:'2-digit',second:'2-digit'});
                function card(id,title){return document.getElementById(id);}
                function draw(canvasId,values,color,label){
                  const c=document.getElementById(canvasId); if(!c||!values||!values.length) return;
                  const dpr=window.devicePixelRatio||1; c.width=c.clientWidth*dpr; c.height=c.clientHeight*dpr;
                  const g=c.getContext('2d'); g.scale(dpr,dpr);
                  const w=c.clientWidth,h=c.clientHeight,max=Math.max.apply(null,values)||1,min=0;
                  g.clearRect(0,0,w,h);
                  g.strokeStyle='#ffffff12'; g.lineWidth=1;
                  for(let i=0;i<=4;i++){const y=h/4*i;g.beginPath();g.moveTo(0,y);g.lineTo(w,y);g.stroke();}
                  const grad=g.createLinearGradient(0,0,w,0); grad.addColorStop(0,color[0]); grad.addColorStop(1,color[1]);
                  g.strokeStyle=grad; g.lineWidth=2; g.beginPath();
                  values.forEach((v,i)=>{const x=w*i/(values.length-1||1); const y=h-(v-min)/(max-min||1)*(h-12)-6;
                    if(i===0)g.moveTo(x,y); else g.lineTo(x,y);});
                  g.stroke();
                  g.fillStyle='#a6aec2'; g.font='12px ui-sans-serif,system-ui'; g.fillText(label+' · макс '+Math.round(max),8,16);
                }
                function act(name,doValue){
                  return api('/api/action/'+name+'?do='+encodeURIComponent(doValue)).then(r=>{refresh();});
                }
                async function refresh(){
                  const s=await api('/api/state');
                  const $=(id)=>document.getElementById(id);
                  if($('fps')) $('fps').textContent=Math.round(s.fps||0);
                  if($('ping')) $('ping').textContent=(s.online?'в игре':'меню');
                  if($('player')) $('player').textContent=s.player||'—';
                  if($('coords')) $('coords').textContent=s.player_state?(s.player_state.x+' '+s.player_state.y+' '+s.player_state.z):'—';
                  if($('world')) $('world').textContent=s.player_state?s.player_state.world:'—';
                  if($('hp')){const st=s.player_state; $('hp').textContent=st?(st.health+' / '+st.maxHealth):'—';
                    const bar=$('hpbar'); if(bar&&st) bar.style.width=Math.max(0,Math.min(100,st.health/st.maxHealth*100))+'%';}
                  if(s.session){ if($('playtime')) $('playtime').textContent=fmtMs(s.session.playtimeMs);
                    if($('distance')) $('distance').textContent=s.session.distance+' блоков';
                    if($('blocks')) $('blocks').textContent=s.session.blocks;
                    if($('kills')) $('kills').textContent=s.session.kills;}
                  if(s.music){ const m=s.music; if($('track')) $('track').textContent=m.title||'ничего не играет';
                    if($('trackbar')) $('trackbar').style.width=Math.max(0,Math.min(100,m.progress))+'%';
                    if($('mstate')) $('mstate').textContent=m.playing?(m.paused?'пауза':'играет'):'выключен';
                    if($('mqueue')) $('mqueue').textContent=m.queue;}
                  if(s.modules){ if($('modules')) $('modules').textContent=s.modules.enabled+' / '+s.modules.total;}
                  if($('deaths')) $('deaths').textContent=s.deaths==null?'0':s.deaths;
                  const e=await api('/api/events');
                  const feed=$('feed');
                  if(feed&&e.events){ feed.innerHTML=e.events.map(x=>'<div class="item"><span class="time">'+fmtTime(x.time)+
                    '</span><span class="tag">'+x.kind+'</span><span>'+x.text+'</span></div>').join('')||
                    '<div class="muted">пока пусто</div>'; }
                  if(document.getElementById('rating')) await refreshDash();
                }
                async function refreshDash(){
                  const st=await api('/api/stats');
                  draw('fpsChart',st.fps,['#7cc4ff','#b98bff'],'FPS');
                  draw('memChart',st.memory,['#6cd08a','#7cc4ff'],'Память, МБ');
                  const tb=document.querySelector('#rating tbody');
                  if(tb&&st.rating){ tb.innerHTML=st.rating.slice().reverse().map(r=>'<tr><td>'+fmtTime(r.time)+
                    '</td><td>'+r.minutes+' мин</td><td>'+r.distance+'</td><td>'+r.blocks+'</td><td>'+r.kills+'</td></tr>').join('')
                    || '<tr><td colspan="5" class="muted">пока нет данных</td></tr>'; }
                }
                refresh(); setInterval(refresh,2000);
                """.replace(TOKEN_PLACEHOLDER, LocalHttp.get().token());
    }

    /** Мобильная страница: главное — одним экраном, кнопки крупные. */
    public static String companion(String query) {
        return page("ByAzen · компаньон", """
                <header>
                  <div class="brand"><div class="logo">B</div>
                    <div><h1>ByAzen</h1><div class="muted">мобильный компаньон</div></div></div>
                  <div class="pill on" id="ping">—</div>
                </header>
                <div class="grid">
                  <div class="card"><h2>Кадры</h2><div class="big" id="fps">—</div>
                    <div class="muted">FPS в клиенте</div></div>
                  <div class="card"><h2>Игрок</h2><div class="big" id="player">—</div>
                    <div class="row"><span>Координаты</span><span id="coords">—</span></div>
                    <div class="row"><span>Мир</span><span id="world">—</span></div></div>
                  <div class="card"><h2>Здоровье</h2><div class="big" id="hp">—</div>
                    <div class="bar"><i id="hpbar" style="width:0%"></i></div>
                    <div class="row"><span>Смертей за сессию</span><span id="deaths">0</span></div></div>
                  <div class="card"><h2>Сессия</h2>
                    <div class="row"><span>В игре</span><span id="playtime">—</span></div>
                    <div class="row"><span>Пройдено</span><span id="distance">—</span></div>
                    <div class="row"><span>Добыто</span><span id="blocks">—</span></div>
                    <div class="row"><span>Мобов убито</span><span id="kills">—</span></div>
                    <div class="row"><span>Модулей</span><span id="modules">—</span></div></div>
                  <div class="card"><h2>Музыка</h2><div id="track" class="big" style="font-size:18px">—</div>
                    <div class="bar"><i id="trackbar" style="width:0%"></i></div>
                    <div class="row"><span>Состояние</span><span id="mstate">—</span></div>
                    <div class="row"><span>В очереди</span><span id="mqueue">0</span></div>
                    <div class="controls">
                      <button class="primary" onclick="act('music','toggle')">Пауза</button>
                      <button onclick="act('music','prev')">Назад</button>
                      <button onclick="act('music','next')">Вперёд</button>
                      <button onclick="act('music','stop')">Стоп</button>
                      <button onclick="act('music','volume_down')">Тише</button>
                      <button onclick="act('music','volume_up')">Громче</button>
                    </div></div>
                </div>
                <div class="card"><h2>Лента</h2><div class="feed" id="feed"><div class="muted">загрузка…</div></div></div>
                <footer>ByAzen · страница живёт только пока открыт клиент. Закройте модуль — сервер выключится.</footer>
                """, query, false);
    }

    /** Дашборд: графики, статистика, локальный рейтинг сессий. */
    public static String dashboard(String query) {
        return page("ByAzen · дашборд", """
                <header>
                  <div class="brand"><div class="logo">B</div>
                    <div><h1>Дашборд ByAzen</h1><div class="muted">статистика, музыка, рейтинг сессий</div></div></div>
                  <div class="pill" id="ping">—</div>
                </header>
                <div class="grid">
                  <div class="card"><h2>FPS</h2><canvas id="fpsChart"></canvas></div>
                  <div class="card"><h2>Память</h2><canvas id="memChart"></canvas></div>
                </div>
                <div class="grid">
                  <div class="card"><h2>Сейчас</h2>
                    <div class="row"><span>Игрок</span><span id="player">—</span></div>
                    <div class="row"><span>Координаты</span><span id="coords">—</span></div>
                    <div class="row"><span>Здоровье</span><span id="hp">—</span></div>
                    <div class="row"><span>Модулей включено</span><span id="modules">—</span></div></div>
                  <div class="card"><h2>Сессия</h2>
                    <div class="row"><span>В игре</span><span id="playtime">—</span></div>
                    <div class="row"><span>Пройдено</span><span id="distance">—</span></div>
                    <div class="row"><span>Добыто</span><span id="blocks">—</span></div>
                    <div class="row"><span>Мобов убито</span><span id="kills">—</span></div></div>
                  <div class="card"><h2>Музыка</h2>
                    <div id="track">—</div>
                    <div class="controls">
                      <button class="primary" onclick="act('music','toggle')">Пауза</button>
                      <button onclick="act('music','next')">Вперёд</button>
                      <button onclick="act('music','stop')">Стоп</button></div></div>
                </div>
                <div class="card"><h2>Локальный рейтинг сессий</h2>
                  <table id="rating"><thead><tr><th>Время</th><th>Длительность</th><th>Пройдено</th><th>Добыто</th><th>Убийств</th></tr></thead>
                  <tbody></tbody></table>
                  <div class="muted">Итоги сохраняются каждую минуту — так видно, какая сессия была самой результативной.</div></div>
                <div class="card"><h2>Быстрые действия</h2>
                  <div class="controls">
                    <button onclick="api('/api/action/sound').then(refresh)">Звук в клиенте</button>
                    <button onclick="api('/api/action/notify?value='+encodeURIComponent('Привет из браузера')).then(refresh)">Уведомление</button>
                  </div></div>
                <div class="card"><h2>Лента</h2><div class="feed" id="feed"><div class="muted">загрузка…</div></div></div>
                <footer>ByAzen · данные не уходят в интернет: страница отдаётся вашим же клиентом.</footer>
                """, query, true);
    }

    public static String denied() {
        return page("ByAzen · доступ закрыт", """
                <header><div class="brand"><div class="logo">B</div><div><h1>Нужен токен</h1>
                  <div class="muted">ссылка без ключа не пускает</div></div></div></header>
                <div class="card">Откройте ссылку с QR-кода из игры (модуль «Мобильный компаньон»).</div>
                """, null, false);
    }

    public static String off() {
        return page("ByAzen · страница выключена", """
                <header><div class="brand"><div class="logo">B</div><div><h1>Страница выключена</h1>
                  <div class="muted">включите модуль в клиенте</div></div></div></header>
                <div class="card">Эта страница отдаётся, только когда включён соответствующий модуль ByAzen.</div>
                """, null, false);
    }

    private static String page(String title, String body, String query, boolean dashboardLink) {
        String token = LocalHttp.get().token();
        String head = "<!doctype html><html lang=\"ru\"><head><meta charset=\"utf-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">"
                + "<title>" + title + "</title><style>" + WebPages.style() + "</style></head><body><div class=\"wrap\">";
        String links = dashboardLink
                ? "<div class=\"controls\"><a class=\"pill\" href=\"/?t=" + token + "\">Компаньон</a>"
                + "<a class=\"pill\" href=\"/dashboard?t=" + token + "\">Дашборд</a></div>"
                : "";
        return head + links + body + "</div><script>" + WebPages.script() + "</script></body></html>";
    }

    /** Короткая строка состояния для чата. */
    public static String summary() {
        return String.format(Locale.ROOT, "%s · запросов %d", LocalHttp.get().status(), LocalHttp.get().requests());
    }
}

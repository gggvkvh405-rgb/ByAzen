package org.zenith.base.bot.net;

import com.google.common.base.Suppliers;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.flow.FlowControlHandler;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.ClosedChannelException;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.crypto.Cipher;
import net.minecraft.SharedConstants;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.state.NetworkState;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.encryption.PacketDecryptor;
import net.minecraft.network.encryption.PacketEncryptor;
import net.minecraft.network.handler.EncoderHandler;
import net.minecraft.network.handler.NetworkStateTransitions;
import net.minecraft.network.handler.NetworkStateTransitions.DecoderTransitioner;
import net.minecraft.network.handler.NetworkStateTransitions.EncoderTransitioner;
import net.minecraft.network.handler.NetworkStateTransitions.InboundConfigurer;
import net.minecraft.network.handler.PacketBundleHandler;
import net.minecraft.network.handler.PacketBundler;
import net.minecraft.network.handler.PacketDeflater;
import net.minecraft.network.handler.PacketEncoderException;
import net.minecraft.network.handler.PacketInflater;
import net.minecraft.network.handler.PacketUnbundler;
import net.minecraft.network.handler.SizePrepender;
import net.minecraft.network.handler.SplitterHandler;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.handshake.ConnectionIntent;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.state.HandshakeStates;
import net.minecraft.network.state.LoginStates;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.zenith.base.bot.via.BotVia;

public final class BotConnection extends SimpleChannelInboundHandler<Packet<?>> {
   public static final Logger LOGGER = LogUtils.getLogger();
   public static final int READ_TIMEOUT_SECONDS = 30;
   public static final Supplier<NioEventLoopGroup> IO_GROUP = Suppliers.memoize(
      () -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Bot Netty IO #%d").setDaemon(true).build())
   );
   public final Queue<Consumer<BotConnection>> queuedTasks = Queues.newConcurrentLinkedQueue();
   public Channel channel;
   public SocketAddress address;
   public volatile PacketListener prePlayStateListener;
   public volatile PacketListener packetListener;
   public DisconnectionInfo disconnectionInfo;
   public volatile DisconnectionInfo pendingDisconnectionInfo;
   public boolean encrypted;
   public boolean disconnected;
   public boolean errored;
   public int viaProtocolVersion = -1;

   protected void channelRead0(ChannelHandlerContext var1, Packet<?> var2) {
      if (this.channel != null && this.channel.isOpen()) {
         PacketListener packetlistener = this.packetListener;
         if (packetlistener == null) {
            throw new IllegalStateException("Received a packet before the packet listener was initialized");
         }

         if (packetlistener.accepts(var2)) {
            if (packetlistener instanceof BotCommonHandler botcommonhandler) {
               botcommonhandler.firePacketEvent(var2);
            }

            try {
               handlePacket(var2, packetlistener);
            } catch (OffThreadException var5) {
            } catch (RejectedExecutionException rejectedexecutionexception) {
               this.disconnect(Text.translatable("multiplayer.disconnect.server_shutdown"));
            } catch (ClassCastException classcastexception) {
               LOGGER.error("Received {} that couldn't be processed", var2.getClass(), classcastexception);
               this.disconnect(Text.translatable("multiplayer.disconnect.invalid_packet"));
            }
         }
      }
   }

   public static BotConnection connect(String var0, int var1, String var2) throws UnknownHostException {
      return connect(var0, var1, var2, -1);
   }

   public static BotConnection connect(String var0, int var1, String var2, int var3) throws UnknownHostException {
      BotConnection botconnection = new BotConnection();
      if (BotVia.isTranslationNeeded(var3)) {
         if (BotVia.ensureInitialized()) {
            botconnection.viaProtocolVersion = var3;
         } else {
            LOGGER.warn("ViaVersion unavailable, connecting to {}:{} with native protocol", var0, var1);
         }
      }

      BotProxy botproxy = BotProxy.parse(var2);
      if (botproxy == null) {
         InetSocketAddress inetsocketaddress = resolve(var0, var1);
         botconnection.bootstrap(null).connect(inetsocketaddress.getAddress(), inetsocketaddress.getPort()).syncUninterruptibly();
      } else {
         ProxyHandler proxyhandler = botproxy.createHandler();
         botconnection.bootstrap(proxyhandler).connect(InetSocketAddress.createUnresolved(var0, var1)).syncUninterruptibly();
         proxyhandler.connectFuture().syncUninterruptibly();
      }

      return botconnection;
   }

   public static InetSocketAddress resolve(String var0, int var1) throws UnknownHostException {
      ServerAddress serveraddress = new ServerAddress(var0, var1);
      return AllowedAddressResolver.DEFAULT
         .resolve(serveraddress)
         .<InetSocketAddress>map(Address::getInetSocketAddress)
         .orElseThrow(() -> new UnknownHostException(var0));
   }

   public Bootstrap bootstrap(ProxyHandler var1) {
      return (Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)IO_GROUP.get())).channel(NioSocketChannel.class))
         .handler(new ConnectionChannelInitializer(this, var1));
   }

   public static void addClientHandlers(ChannelPipeline var0) {
      var0.addLast("splitter", new SplitterHandler(null))
         .addLast(new ChannelHandler[]{new FlowControlHandler()})
         .addLast("inbound_config", new InboundConfigurer())
         .addLast("prepender", new SizePrepender())
         .addLast("encoder", new EncoderHandler(HandshakeStates.C2S));
   }

   public void addFlowControlHandler(ChannelPipeline var1) {
      var1.addLast("packet_handler", this);
   }

   public void startLogin(String var1, int var2, ClientLoginPacketListener var3) {
      this.prePlayStateListener = var3;
      this.submit(var3xx -> {
         var3xx.transitionInbound(LoginStates.S2C, var3);
         var3xx.sendImmediately(new HandshakeC2SPacket(SharedConstants.getGameVersion().protocolVersion(), var1, var2, ConnectionIntent.LOGIN), null, true);
         var3xx.transitionOutbound(LoginStates.C2S);
      });
   }

   public void channelActive(ChannelHandlerContext var1) throws Exception {
      super.channelActive(var1);
      this.channel = var1.channel();
      this.address = this.channel.remoteAddress();
      DisconnectionInfo disconnectioninfo = this.pendingDisconnectionInfo;
      if (disconnectioninfo != null) {
         this.disconnect(disconnectioninfo);
      }
   }

   public void channelInactive(ChannelHandlerContext var1) {
      this.disconnect(Text.translatable("disconnect.endOfStream"));
   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) {
      if (var2 instanceof PacketEncoderException) {
         LOGGER.debug("Skipping packet due to errors", var2.getCause());
      } else {
         boolean flag = !this.errored;
         this.errored = true;
         if (this.channel != null && this.channel.isOpen()) {
            if (var2 instanceof TimeoutException) {
               LOGGER.debug("Timeout", var2);
               this.disconnect(Text.translatable("disconnect.timeout"));
            } else {
               MutableText mutabletext = Text.translatable("disconnect.genericReason", new Object[]{"Internal Exception: " + var2});
               PacketListener packetlistener = this.packetListener;
               DisconnectionInfo disconnectioninfo = packetlistener != null ? packetlistener.createDisconnectionInfo(mutabletext, var2) : new DisconnectionInfo(mutabletext);
               if (flag) {
                  LOGGER.debug("Failed to send packet", var2);
                  this.disconnect(disconnectioninfo);
                  this.tryDisableAutoRead();
               } else {
                  LOGGER.debug("Double fault", var2);
                  this.disconnect(disconnectioninfo);
               }
            }
         }
      }
   }

   public static <T extends PacketListener> void handlePacket(Packet<T> var0, PacketListener var1) {
      var0.apply((T)var1);
   }

   public <T extends PacketListener> void transitionInbound(NetworkState<T> var1, T var2) {
      Objects.requireNonNull(var2, "packetListener");
      if (var1.side() != NetworkSide.CLIENTBOUND) {
         throw new IllegalStateException("Invalid inbound protocol: " + var1.id());
      }

      if (var1.id() != var2.getPhase()) {
         throw new IllegalStateException("Listener protocol (" + var2.getPhase() + ") does not match requested one " + var1);
      }

      this.packetListener = var2;
      this.prePlayStateListener = null;
      DecoderTransitioner decodertransitioner = NetworkStateTransitions.decoderTransitioner(var1);
      PacketBundleHandler packetbundlehandler = var1.bundleHandler();
      if (packetbundlehandler != null) {
         PacketBundler packetbundler = new PacketBundler(packetbundlehandler);
         decodertransitioner = decodertransitioner.andThen(var1x -> var1x.pipeline().addAfter("decoder", "bundler", packetbundler));
      }

      syncUninterruptibly(this.channel.writeAndFlush(decodertransitioner));
   }

   public void transitionOutbound(NetworkState<?> var1) {
      if (var1.side() != NetworkSide.SERVERBOUND) {
         throw new IllegalStateException("Invalid outbound protocol: " + var1.id());
      }

      EncoderTransitioner encodertransitioner = NetworkStateTransitions.encoderTransitioner(var1);
      PacketBundleHandler packetbundlehandler = var1.bundleHandler();
      if (packetbundlehandler != null) {
         PacketUnbundler packetunbundler = new PacketUnbundler(packetbundlehandler);
         encodertransitioner = encodertransitioner.andThen(var1x -> var1x.pipeline().addAfter("encoder", "unbundler", packetunbundler));
      }

      syncUninterruptibly(this.channel.writeAndFlush(encodertransitioner));
   }

   public static void syncUninterruptibly(ChannelFuture var0) {
      try {
         var0.syncUninterruptibly();
      } catch (Exception exception) {
         if (!(exception instanceof ClosedChannelException)) {
            throw exception;
         }

         LOGGER.info("Connection closed during protocol change");
      }
   }

   public void send(Packet<?> var1) {
      this.send(var1, null);
   }

   public void send(Packet<?> var1, ChannelFutureListener var2) {
      this.send(var1, var2, true);
   }

   public void send(Packet<?> var1, ChannelFutureListener var2, boolean var3) {
      if (this.isOpen()) {
         this.handleQueuedTasks();
         this.sendImmediately(var1, var2, var3);
      } else {
         this.queuedTasks.add(var3xx -> var3xx.sendImmediately(var1, var2, var3));
      }
   }

   public void submit(Consumer<BotConnection> var1) {
      if (this.isOpen()) {
         this.handleQueuedTasks();
         var1.accept(this);
      } else {
         this.queuedTasks.add(var1);
      }
   }

   public void sendImmediately(Packet<?> var1, ChannelFutureListener var2, boolean var3) {
      if (this.channel.eventLoop().inEventLoop()) {
         this.sendInternal(var1, var2, var3);
      } else {
         this.channel.eventLoop().execute(() -> this.sendInternal(var1, var2, var3));
      }
   }

   public void sendInternal(Packet<?> var1, ChannelFutureListener var2, boolean var3) {
      ChannelFuture channelfuture = var3 ? this.channel.writeAndFlush(var1) : this.channel.write(var1);
      if (var2 != null) {
         channelfuture.addListener(var2);
      }

      channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }

   public void handleQueuedTasks() {
      if (this.channel != null && this.channel.isOpen()) {
         Consumer consumer;
         synchronized (this.queuedTasks) {
            while ((consumer = this.queuedTasks.poll()) != null) {
               consumer.accept(this);
            }
         }
      }
   }

   public void tick() {
      this.handleQueuedTasks();
      if (this.packetListener instanceof TickablePacketListener tickablepacketlistener) {
         tickablepacketlistener.tick();
      }

      if (!this.isOpen() && !this.disconnected) {
         this.handleDisconnection();
      }

      if (this.channel != null) {
         this.channel.flush();
      }
   }

   public void disconnect(Text var1) {
      this.disconnect(new DisconnectionInfo(var1));
   }

   public void disconnect(DisconnectionInfo var1) {
      if (this.channel == null) {
         this.pendingDisconnectionInfo = var1;
      }

      if (this.isOpen()) {
         this.channel.close().awaitUninterruptibly();
         this.disconnectionInfo = var1;
      }
   }

   public void setupEncryption(Cipher var1, Cipher var2) {
      this.encrypted = true;
      this.channel.pipeline().addBefore("splitter", "decrypt", new PacketDecryptor(var1));
      this.channel.pipeline().addBefore("prepender", "encrypt", new PacketEncryptor(var2));
   }

   public boolean isEncrypted() {
      return this.encrypted;
   }

   public void setCompressionThreshold(int var1, boolean var2) {
      if (var1 >= 0) {
         if (this.channel.pipeline().get("decompress") instanceof PacketInflater packetinflater) {
            packetinflater.setCompressionThreshold(var1, var2);
         } else {
            this.channel.pipeline().addAfter("splitter", "decompress", new PacketInflater(var1, var2));
         }

         if (this.channel.pipeline().get("compress") instanceof PacketDeflater packetdeflater) {
            packetdeflater.setCompressionThreshold(var1);
         } else {
            this.channel.pipeline().addAfter("prepender", "compress", new PacketDeflater(var1));
         }
      } else {
         if (this.channel.pipeline().get("decompress") instanceof PacketInflater) {
            this.channel.pipeline().remove("decompress");
         }

         if (this.channel.pipeline().get("compress") instanceof PacketDeflater) {
            this.channel.pipeline().remove("compress");
         }
      }
   }

   public void handleDisconnection() {
      if (this.channel != null && !this.channel.isOpen()) {
         if (this.disconnected) {
            LOGGER.warn("handleDisconnection() called twice");
            return;
         }

         this.disconnected = true;
         PacketListener packetlistener = this.packetListener;
         PacketListener packetlistener1 = packetlistener != null ? packetlistener : this.prePlayStateListener;
         if (packetlistener1 != null) {
            DisconnectionInfo disconnectioninfo = Objects.requireNonNullElseGet(
               this.disconnectionInfo, () -> new DisconnectionInfo(Text.translatable("multiplayer.disconnect.generic"))
            );
            packetlistener1.onDisconnected(disconnectioninfo);
         }
      }
   }

   public void tryDisableAutoRead() {
      if (this.channel != null) {
         this.channel.config().setAutoRead(false);
      }
   }

   public boolean isOpen() {
      return this.channel != null && this.channel.isOpen();
   }

   public PacketListener getPacketListener() {
      return this.packetListener;
   }

   public DisconnectionInfo getDisconnectionInfo() {
      return this.disconnectionInfo;
   }

   public SocketAddress getAddress() {
      return this.address;
   }

   private static final class ConnectionChannelInitializer extends ChannelInitializer<Channel> {
      private final BotConnection connection;
      private final ProxyHandler proxyHandler;

      private ConnectionChannelInitializer(BotConnection connection, ProxyHandler proxyHandler) {
         this.connection = connection;
         this.proxyHandler = proxyHandler;
      }

      @Override
      protected void initChannel(Channel channel) {
         try {
            channel.config().setOption(ChannelOption.TCP_NODELAY, true);
         } catch (ChannelException ignored) {
         }

         ChannelPipeline pipeline = channel.pipeline();
         pipeline.addLast("timeout", new ReadTimeoutHandler(READ_TIMEOUT_SECONDS));
         if (this.proxyHandler != null) {
            pipeline.addLast("proxy", this.proxyHandler);
         }

         addClientHandlers(pipeline);
         this.connection.addFlowControlHandler(pipeline);
         if (this.connection.viaProtocolVersion > 0) {
            BotVia.injectPipeline(channel, this.connection.viaProtocolVersion);
         }
      }
   }
}

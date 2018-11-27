/*
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.plugin.netty;

import com.navercorp.pinpoint.bootstrap.async.AsyncContextAccessor;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentClass;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentException;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentMethod;
import com.navercorp.pinpoint.bootstrap.instrument.Instrumentor;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformCallback;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplate;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplateAware;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.ExecutionPolicy;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPlugin;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPluginSetupContext;
import com.navercorp.pinpoint.bootstrap.plugin.util.InstrumentUtils;
import com.navercorp.pinpoint.plugin.netty.transformer.http.HttpEncoderTransformer;
import com.navercorp.pinpoint.plugin.netty.transformer.http.HttpRequestTransformer;

import java.security.ProtectionDomain;

import static com.navercorp.pinpoint.common.util.VarArgs.va;

/**
 * @author Taejin Koo
 */
public class NettyPlugin implements ProfilerPlugin, TransformTemplateAware {

    private static final PLogger LOGGER = PLoggerFactory.getLogger(NettyPlugin.class);
    private static final boolean IS_DEBUG = LOGGER.isDebugEnabled();

    private TransformTemplate transformTemplate;


    @Override
    public void setup(ProfilerPluginSetupContext context) {
        NettyConfig config = new NettyConfig(context.getConfig());
        if (!config.isPluginEnable()) {
            LOGGER.info("Disable netty option. 'profiler.netty=false'");
            return;
        }

        transformTemplate.transform("io.netty.bootstrap.Bootstrap", new BootstrapTransformer());
        transformTemplate.transform("io.netty.channel.DefaultChannelPipeline", new ChannelPipelineTransformer());
        transformTemplate.transform("io.netty.util.concurrent.DefaultPromise", new PromiseTransformer());
        transformTemplate.transform("io.netty.channel.DefaultChannelPromise", new ChannelPromiseTransformer());

        // codec
        if (config.isHttpCodecEnable()) {
            // addHttpServerRequestImpl();
            transformTemplate.transform("io.netty.handler.codec.http.DefaultHttpRequest", new HttpRequestTransformer());
            transformTemplate.transform("io.netty.handler.codec.http.HttpObjectEncoder", new HttpEncoderTransformer());
        }
    }

    @Override
    public void setTransformTemplate(TransformTemplate transformTemplate) {
        this.transformTemplate = transformTemplate;
    }

    private static class BootstrapTransformer implements TransformCallback {

        @Override
        public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
            InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);

            final InstrumentMethod connectMethod = target.getDeclaredMethod("connect");
            if (connectMethod != null) {
                connectMethod.addScopedInterceptor(NettyConstants.INTERCEPTOR_BOOTSTRAP_CONNECT, NettyConstants.SCOPE, ExecutionPolicy.BOUNDARY);
            } else {
                if (IS_DEBUG) {
                    LOGGER.debug("can't find connect method");
                }
            }

            final InstrumentMethod connectMethod2 = target.getDeclaredMethod("connect", "java.net.SocketAddress");
            if (connectMethod2 != null) {
                connectMethod2.addScopedInterceptor(NettyConstants.INTERCEPTOR_BOOTSTRAP_CONNECT, NettyConstants.SCOPE, ExecutionPolicy.BOUNDARY);
            } else {
                if (IS_DEBUG) {
                    LOGGER.debug("can't find connect(\"java.net.SocketAddress\") method");
                }
            }

            final InstrumentMethod connectMethod3 = target.getDeclaredMethod("connect", "java.net.SocketAddress", "java.net.SocketAddress");
            if (connectMethod3 != null) {
                connectMethod3.addScopedInterceptor(NettyConstants.INTERCEPTOR_BOOTSTRAP_CONNECT, NettyConstants.SCOPE, ExecutionPolicy.BOUNDARY);
            } else {
                if (IS_DEBUG) {
                    LOGGER.debug("can't find connect(\"java.net.SocketAddress\", \"java.net.SocketAddress\") method");
                }
            }

            return target.toBytecode();
        }
    }

    private static class ChannelPipelineTransformer implements TransformCallback {

        @Override
        public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
            InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);

            final InstrumentMethod writeMethod1 = target.getDeclaredMethod("write", "java.lang.Object");
            if (writeMethod1 != null) {
                writeMethod1.addScopedInterceptor(NettyConstants.INTERCEPTOR_CHANNEL_PIPELINE_WRITE, NettyConstants.SCOPE_WRITE, ExecutionPolicy.BOUNDARY);
            } else {
                if (IS_DEBUG) {
                    LOGGER.debug("can't find write(\"java.lang.Object\") method");
                }
            }

            final InstrumentMethod writeMethod2 = target.getDeclaredMethod("write", "java.lang.Object", "io.netty.channel.ChannelPromise");
            if (writeMethod2 != null) {
                writeMethod2.addScopedInterceptor(NettyConstants.INTERCEPTOR_CHANNEL_PIPELINE_WRITE, NettyConstants.SCOPE_WRITE, ExecutionPolicy.BOUNDARY);
            } else {
                if (IS_DEBUG) {
                    LOGGER.debug("can't find write(\"java.lang.Object\", \"io.netty.channel.ChannelPromise\") method");
                }
            }

            final InstrumentMethod writeAndFlushMethod1 = target.getDeclaredMethod("writeAndFlush", "java.lang.Object");
            if (writeAndFlushMethod1 != null) {
                writeAndFlushMethod1.addScopedInterceptor(NettyConstants.INTERCEPTOR_CHANNEL_PIPELINE_WRITE, NettyConstants.SCOPE_WRITE, ExecutionPolicy.BOUNDARY);
            } else {
                if (IS_DEBUG) {
                    LOGGER.debug("can't find writeAndFlush(\"java.lang.Object\") method");
                }
            }

            final InstrumentMethod writeAndFlushMethod2 = target.getDeclaredMethod("writeAndFlush", "java.lang.Object", "io.netty.channel.ChannelPromise");
            if (writeAndFlushMethod2 != null) {
                writeAndFlushMethod2.addScopedInterceptor(NettyConstants.INTERCEPTOR_CHANNEL_PIPELINE_WRITE, NettyConstants.SCOPE_WRITE, ExecutionPolicy.BOUNDARY);
            } else {
                if (IS_DEBUG) {
                    LOGGER.debug("can't find writeAndFlush(\"java.lang.Object\", \"io.netty.channel.ChannelPromise\") method");
                }
            }

            return target.toBytecode();
        }
    }

    private static class PromiseTransformer implements TransformCallback {

        @Override
        public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
            InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);
            target.addField(AsyncContextAccessor.class.getName());

            InstrumentMethod notifyListenersNowMethod = target.getDeclaredMethod("notifyListenersNow");
            if (notifyListenersNowMethod != null) {
                notifyListenersNowMethod.addInterceptor(NettyConstants.INTERCEPTOR_CHANNEL_PROMISE_NOTIFY);
            } else {
                if (IS_DEBUG) {
                    LOGGER.debug("can't find notifyListenersNow method");
                }
            }


            InstrumentMethod notifyListener0Method = target.getDeclaredMethod("notifyListener0", "io.netty.util.concurrent.Future", "io.netty.util.concurrent.GenericFutureListener");
            if (notifyListener0Method != null) {
                notifyListener0Method.addInterceptor(NettyConstants.INTERCEPTOR_BASIC, va(NettyConstants.SERVICE_TYPE_INTERNAL));
            } else {
                if (IS_DEBUG) {
                    LOGGER.debug("can't find notifyListener0 method");
                }
            }

            return target.toBytecode();
        }
    }

    private static class ChannelPromiseTransformer implements TransformCallback {

        @Override
        public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
            InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);

            final InstrumentMethod addListenerMethod1 = target.getDeclaredMethod("addListener", "io.netty.util.concurrent.GenericFutureListener");
            if (addListenerMethod1 != null) {
                addListenerMethod1.addScopedInterceptor(NettyConstants.INTERCEPTOR_CHANNEL_PROMISE_ADD_LISTENER, NettyConstants.SCOPE, ExecutionPolicy.BOUNDARY);
            } else {
                if (IS_DEBUG) {
                    LOGGER.debug("can't find addListener method");
                }
            }

            final InstrumentMethod addListenerMethod2 = target.getDeclaredMethod("addListeners", "io.netty.util.concurrent.GenericFutureListener[]");
            if (addListenerMethod2 != null) {
                addListenerMethod2.addScopedInterceptor(NettyConstants.INTERCEPTOR_CHANNEL_PROMISE_ADD_LISTENER, NettyConstants.SCOPE, ExecutionPolicy.BOUNDARY);
            } else {
                if (IS_DEBUG) {
                    LOGGER.debug("can't find addListeners method");
                }
            }

            return target.toBytecode();
        }

    }

}

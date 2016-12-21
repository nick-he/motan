/*
 *  Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.weibo.api.motan.filter.opentracing;

import java.util.Iterator;
import java.util.ServiceLoader;

import com.weibo.api.motan.util.LoggerUtil;

import io.opentracing.Tracer;
/**
 * 
 * @Description TracerFactory
 * @author zhanglei
 * @date Dec 8, 2016
 *
 */
public interface TracerFactory {
    public static final TracerFactory DEFAULT = new DefaultTracerFactory();

    /**
     * get a Tracer implementation. this method may called every request, consider whether singleton
     * pattern is needed
     * 
     * @return
     */
    Tracer getTracer();

    class DefaultTracerFactory implements TracerFactory {
        private static Tracer tracer = null;

        static {
            loadDefaultTracer();
        }

        /**
         * load SPI Tracer and set default. the first tracer was used if more than one tracer was
         * found.
         */
        private static void loadDefaultTracer() {
            try {
                Iterator<Tracer> implementations = ServiceLoader.load(Tracer.class, Tracer.class.getClassLoader()).iterator();
                if (implementations.hasNext()) {
                    tracer = implementations.next();
                    LoggerUtil.info("io.opentracing.Tracer load in DefaultTracerFactory, " + tracer.getClass().getSimpleName()
                            + " is used as default tracer.");
                }
            } catch (Exception e) {
                LoggerUtil.warn("DefaultTracerFactory load Tracer fail.", e);
            }
        }


        @Override
        public Tracer getTracer() {
            return tracer;
        }

    }
}
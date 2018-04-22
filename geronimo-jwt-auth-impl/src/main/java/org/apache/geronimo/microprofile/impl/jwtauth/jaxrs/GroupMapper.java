/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.geronimo.microprofile.impl.jwtauth.jaxrs;

import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.geronimo.microprofile.impl.jwtauth.config.GeronimoJwtAuthConfig;
import org.apache.geronimo.microprofile.impl.jwtauth.io.PropertiesLoader;

@ApplicationScoped
class GroupMapper {
    @Inject
    private GeronimoJwtAuthConfig config;

    private final Map<String, Collection<String>> mapping = new HashMap<>();

    @PostConstruct
    private void init() {
        ofNullable(config.read("groups.mapping", null))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(PropertiesLoader::load)
                .ifPresent(props -> props.stringPropertyNames()
                        .forEach(k -> mapping.put(k, Stream.of(props.getProperty(k).split(","))
                                .map(String::trim)
                                .collect(toSet()))));

    }

    Collection<String> map(final String tokenName) {
        return ofNullable(mapping.get(tokenName)).orElse(singletonList(tokenName));
    }
}
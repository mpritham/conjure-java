/*
 * (c) Copyright 2019 Palantir Technologies Inc. All rights reserved.
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

package com.palantir.conjure.java.undertow.runtime;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.palantir.conjure.java.undertow.lib.AuthorizationExtractor;
import com.palantir.conjure.java.undertow.lib.BodySerDe;
import com.palantir.conjure.java.undertow.lib.PlainSerDe;
import com.palantir.conjure.java.undertow.lib.UndertowRuntime;
import com.palantir.logsafe.Preconditions;
import java.util.List;

/**
 * {@link ConjureUndertowRuntime} provides functionality required by generated handlers.
 */
public final class ConjureUndertowRuntime implements UndertowRuntime {

    private final BodySerDe bodySerDe;
    private final AuthorizationExtractor auth;

    private ConjureUndertowRuntime(Builder builder) {
        this.bodySerDe = new ConjureBodySerDe(builder.encodings.isEmpty()
                ? ImmutableList.of(Encodings.json(), Encodings.cbor()) : builder.encodings);
        this.auth = new ConjureAuthorizationExtractor(plainSerDe());
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public BodySerDe bodySerDe() {
        return bodySerDe;
    }

    @Override
    public PlainSerDe plainSerDe() {
        return ConjurePlainSerDe.INSTANCE;
    }

    @Override
    public AuthorizationExtractor auth() {
        return auth;
    }

    public static final class Builder {

        private final List<Encoding> encodings = Lists.newArrayList();

        private Builder() {}

        @CanIgnoreReturnValue
        public Builder encodings(Encoding value) {
            encodings.add(Preconditions.checkNotNull(value, "Value is required"));
            return this;
        }

        public ConjureUndertowRuntime build() {
            return new ConjureUndertowRuntime(this);
        }
    }
}
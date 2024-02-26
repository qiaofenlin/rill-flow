/*
 *  Copyright 2021-2023 Weibo, Inc.
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

package com.weibo.rill.flow.olympicene.storage.redis.lock

import spock.lang.Specification

class ResourceLoaderTest extends Specification {
    def "test loadResourceAsText"() {
        expect:
        ResourceLoader.loadResourceAsText("test.json") == "{\"hello\": \"world\"}"
    }

    def "test loadResourceAsText with non-existing resource"() {
        when:
        ResourceLoader.loadResourceAsText("non-existing.txt")

        then:
        thrown(IOException)
    }

    def "test loadResourceAsText with null resource name"() {
        when:
        ResourceLoader.loadResourceAsText(null)

        then:
        thrown(NullPointerException)
    }
}

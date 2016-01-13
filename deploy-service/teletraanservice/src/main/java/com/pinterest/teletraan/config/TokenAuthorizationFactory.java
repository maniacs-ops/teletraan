/**
 * Copyright 2016 Pinterest, Inc.
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
package com.pinterest.teletraan.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.pinterest.teletraan.TeletraanServiceContext;
import com.pinterest.teletraan.security.RoleAuthorizer;

@JsonTypeName("role")
public class TokenAuthorizationFactory implements AuthorizationFactory {
    @JsonProperty
    private String roleCacheSpec;

    public String getRoleCacheSpec() {
        return roleCacheSpec;
    }

    public void setRoleCacheSpec(String roleCacheSpec) {
        this.roleCacheSpec = roleCacheSpec;
    }

    @Override
    public RoleAuthorizer create(TeletraanServiceContext context) throws Exception {
        return new RoleAuthorizer(context, roleCacheSpec);
    }
}

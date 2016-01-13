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
package com.pinterest.teletraan.resource;

import com.pinterest.deployservice.bean.*;
import com.pinterest.deployservice.dao.EnvironDAO;
import com.pinterest.deployservice.handler.EnvironHandler;
import com.pinterest.teletraan.TeletraanServiceContext;
import com.pinterest.teletraan.security.Authorizer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

@Path("/v1/envs/{envName : [a-zA-Z0-9\\-_]+}/{stageName : [a-zA-Z0-9\\-_]+}/web_hooks")
@Api(tags = "Environments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EnvWebHooks {
    private static final Logger LOG = LoggerFactory.getLogger(EnvWebHooks.class);
    private EnvironDAO environDAO;
    private EnvironHandler environHandler;
    private Authorizer authorizer;

    @Context
    UriInfo uriInfo;

    public EnvWebHooks(TeletraanServiceContext context) {
        environDAO = context.getEnvironDAO();
        environHandler = new EnvironHandler(context);
        authorizer = context.getAuthorizer();
    }

    @GET
    @ApiOperation(
            value = "Get webhooks object",
            notes = "Returns a pre/post webhooks object by given environment and stage names",
            response = EnvWebHookBean.class)
    public EnvWebHookBean get(
            @ApiParam(value = "Environment name", required = true)@PathParam("envName") String envName,
            @ApiParam(value = "Stage name", required = true)
            @PathParam("stageName") String stageName) throws Exception {
        EnvironBean environBean = Utils.getEnvStage(environDAO, envName, stageName);
        return environHandler.getHooks(environBean);
    }

    @PUT
    @ApiOperation(
            value = "Update webhooks",
            notes = "Updates pre/deploy webhooks by given environment and stage names with given webhooks object")
    public void update(@Context SecurityContext sc,
                       @ApiParam(value = "Environment name", required = true)@PathParam("envName") String envName,
                       @ApiParam(value = "Stage name", required = true)@PathParam("stageName") String stageName,
        EnvWebHookBean hookBean) throws Exception {
        EnvironBean environBean = Utils.getEnvStage(environDAO, envName, stageName);
        authorizer.authorize(sc, new Resource(environBean.getEnv_name(), Resource.Type.ENV), Role.OPERATOR);
        String userName = sc.getUserPrincipal().getName();
        environHandler.updateHooks(environBean, hookBean, userName);
        LOG.info("Successfully updated web hooks {} for env {}/{} by {}.",
            hookBean, envName, stageName, userName);
    }
}

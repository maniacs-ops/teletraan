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

import com.pinterest.deployservice.bean.GroupRolesBean;
import com.pinterest.deployservice.bean.Resource;
import com.pinterest.deployservice.bean.Role;
import com.pinterest.deployservice.dao.GroupRolesDAO;
import com.pinterest.teletraan.TeletraanServiceContext;
import com.pinterest.teletraan.security.Authorizer;

import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;

public abstract class GroupRoles {
    private final GroupRolesDAO groupRolesDAO;
    private final Authorizer authorizer;

    @Context
    UriInfo uriInfo;

    public GroupRoles(TeletraanServiceContext context) {
        groupRolesDAO = context.getGroupRolesDAO();
        authorizer = context.getAuthorizer();
    }

    public List<GroupRolesBean> getByResource(String resourceId,
        Resource.Type resourceType) throws Exception {
        return groupRolesDAO.getByResource(resourceId, resourceType);
    }

    public GroupRolesBean getByNameAndResource(String groupName, String resourceId,
        Resource.Type resourceType) throws Exception {
        return groupRolesDAO.getByNameAndResource(groupName, resourceId, resourceType);
    }

    public void update(SecurityContext sc, GroupRolesBean bean, String groupName,
        String resourceId, Resource.Type resourceType) throws Exception {
        authorizer.authorize(sc, new Resource(resourceId, resourceType), Role.ADMIN);
        groupRolesDAO.update(bean, groupName, resourceId, resourceType);
    }

    public Response create(SecurityContext sc, GroupRolesBean bean, String resourceId,
        Resource.Type resourceType) throws Exception {
        authorizer.authorize(sc, new Resource(resourceId, resourceType), Role.ADMIN);
        bean.setResource_id(resourceId);
        bean.setResource_type(resourceType);
        groupRolesDAO.insert(bean);
        GroupRolesBean newBean = groupRolesDAO.getByNameAndResource(bean.getGroup_name(), resourceId, resourceType);
        UriBuilder ub = uriInfo.getAbsolutePathBuilder();
        URI roleUri = ub.path(newBean.getGroup_name()).build();
        return Response.created(roleUri).entity(newBean).build();
    }

    public void delete(SecurityContext sc, String groupName, String resourceId,
        Resource.Type resourceType) throws Exception {
        authorizer.authorize(sc, new Resource(resourceId, resourceType), Role.ADMIN);
        groupRolesDAO.delete(groupName, resourceId, resourceType);
    }
}

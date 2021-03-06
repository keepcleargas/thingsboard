/**
 * Copyright © 2016 The Thingsboard Authors
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
package org.thingsboard.server.extensions.api.plugins;

import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.PluginId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.kv.TsKvQuery;
import org.thingsboard.server.common.msg.cluster.ServerAddress;
import org.thingsboard.server.extensions.api.plugins.msg.PluginToRuleMsg;
import org.thingsboard.server.extensions.api.plugins.msg.TimeoutMsg;
import org.thingsboard.server.extensions.api.plugins.msg.ToDeviceRpcRequest;
import org.thingsboard.server.extensions.api.plugins.rpc.RpcMsg;
import org.thingsboard.server.extensions.api.plugins.ws.PluginWebsocketSessionRef;
import org.thingsboard.server.extensions.api.plugins.ws.msg.PluginWebsocketMsg;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PluginContext {

    PluginId getPluginId();

    void reply(PluginToRuleMsg<?> msg);

    boolean checkAccess(DeviceId deviceId);

    Optional<PluginApiCallSecurityContext> getSecurityCtx();

    /*
        Device RPC API
     */

    Optional<ServerAddress> resolve(DeviceId deviceId);

    void getDevice(DeviceId deviceId, PluginCallback<Device> pluginCallback);

    void sendRpcRequest(ToDeviceRpcRequest msg);

    void scheduleTimeoutMsg(TimeoutMsg<?> timeoutMsg);


    /*
        Websocket API
     */

    void send(PluginWebsocketMsg<?> wsMsg) throws IOException;

    void close(PluginWebsocketSessionRef sessionRef) throws IOException;

    /*
        Plugin RPC API
     */

    void sendPluginRpcMsg(RpcMsg msg);

    /*
        Timeseries API
     */


    void saveTsData(DeviceId deviceId, TsKvEntry entry, PluginCallback<Void> callback);

    void saveTsData(DeviceId deviceId, List<TsKvEntry> entry, PluginCallback<Void> callback);

    List<TsKvEntry> loadTimeseries(DeviceId deviceId, TsKvQuery query);

    void loadLatestTimeseries(DeviceId deviceId, Collection<String> keys, PluginCallback<List<TsKvEntry>> callback);

    void loadLatestTimeseries(DeviceId deviceId, PluginCallback<List<TsKvEntry>> callback);

    /*
        Attributes API
     */

    void saveAttributes(DeviceId deviceId, String attributeType, List<AttributeKvEntry> attributes, PluginCallback<Void> callback);

    Optional<AttributeKvEntry> loadAttribute(DeviceId deviceId, String attributeType, String attributeKey);

    List<AttributeKvEntry> loadAttributes(DeviceId deviceId, String attributeType, List<String> attributeKeys);

    List<AttributeKvEntry> loadAttributes(DeviceId deviceId, String attributeType);

    void removeAttributes(DeviceId deviceId, String scope, List<String> attributeKeys);

    void getCustomerDevices(TenantId tenantId, CustomerId customerId, int limit, PluginCallback<List<Device>> callback);
}

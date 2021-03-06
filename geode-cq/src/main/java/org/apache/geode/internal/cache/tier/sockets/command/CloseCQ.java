/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.tier.sockets.command;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.geode.cache.query.CqException;
import org.apache.geode.cache.query.internal.cq.CqService;
import org.apache.geode.cache.query.internal.cq.InternalCqQuery;
import org.apache.geode.distributed.internal.DistributionStats;
import org.apache.geode.internal.cache.tier.CachedRegionHelper;
import org.apache.geode.internal.cache.tier.Command;
import org.apache.geode.internal.cache.tier.MessageType;
import org.apache.geode.internal.cache.tier.sockets.CacheServerStats;
import org.apache.geode.internal.cache.tier.sockets.ClientProxyMembershipID;
import org.apache.geode.internal.cache.tier.sockets.Message;
import org.apache.geode.internal.cache.tier.sockets.ServerConnection;
import org.apache.geode.internal.i18n.LocalizedStrings;
import org.apache.geode.internal.security.AuthorizeRequest;
import org.apache.geode.internal.security.SecurityService;
import org.apache.geode.security.ResourcePermission.Operation;
import org.apache.geode.security.ResourcePermission.Resource;

public class CloseCQ extends BaseCQCommand {

  private static final CloseCQ singleton = new CloseCQ();

  public static Command getCommand() {
    return singleton;
  }

  private CloseCQ() {
    // nothing
  }

  @Override
  public void cmdExecute(final Message clientMessage, final ServerConnection serverConnection,
      final SecurityService securityService, long start) throws IOException {
    CachedRegionHelper crHelper = serverConnection.getCachedRegionHelper();
    ClientProxyMembershipID id = serverConnection.getProxyID();
    CacheServerStats stats = serverConnection.getCacheServerStats();

    serverConnection.setAsTrue(REQUIRES_RESPONSE);
    serverConnection.setAsTrue(REQUIRES_CHUNKED_RESPONSE);

    start = DistributionStats.getStatTime();
    // Retrieve the data from the message parts
    String cqName = clientMessage.getPart(0).getString();

    if (logger.isDebugEnabled()) {
      logger.debug("{}: Received close CQ request from {} cqName: {}", serverConnection.getName(),
          serverConnection.getSocketString(), cqName);
    }

    // Process the query request
    if (cqName == null) {
      String err =
          LocalizedStrings.CloseCQ_THE_CQNAME_FOR_THE_CQ_CLOSE_REQUEST_IS_NULL.toLocalizedString();
      sendCqResponse(MessageType.CQDATAERROR_MSG_TYPE, err, clientMessage.getTransactionId(), null,
          serverConnection);
      return;
    }

    // Process CQ close request
    try {
      // Append Client ID to CQ name
      CqService cqService = crHelper.getCache().getCqService();
      cqService.start();

      String serverCqName = cqName;
      if (id != null) {
        serverCqName = cqService.constructServerCqName(cqName, id);
      }
      InternalCqQuery cqQuery = cqService.getCq(serverCqName);

      if (cqQuery != null) {
        securityService.authorize(Resource.DATA, Operation.READ, cqQuery.getRegionName());

        AuthorizeRequest authzRequest = serverConnection.getAuthzRequest();
        if (authzRequest != null) {

          if (cqQuery != null) {
            String queryStr = cqQuery.getQueryString();
            Set cqRegionNames = new HashSet();
            cqRegionNames.add(cqQuery.getRegionName());
            authzRequest.closeCQAuthorize(cqName, queryStr, cqRegionNames);
          }

        }

        cqService.closeCq(cqName, id);
      }
      if (cqQuery != null)
        serverConnection.removeCq(cqName, cqQuery.isDurable());
    } catch (CqException cqe) {
      sendCqResponse(MessageType.CQ_EXCEPTION_TYPE, "", clientMessage.getTransactionId(), cqe,
          serverConnection);
      return;
    } catch (Exception e) {
      String err =
          LocalizedStrings.CloseCQ_EXCEPTION_WHILE_CLOSING_CQ_CQNAME_0.toLocalizedString(cqName);
      sendCqResponse(MessageType.CQ_EXCEPTION_TYPE, err, clientMessage.getTransactionId(), e,
          serverConnection);
      return;
    }

    // Send OK to client
    sendCqResponse(MessageType.REPLY,
        LocalizedStrings.CloseCQ_CQ_CLOSED_SUCCESSFULLY.toLocalizedString(),
        clientMessage.getTransactionId(), null, serverConnection);
    serverConnection.setAsTrue(RESPONDED);

    long oldStart = start;
    start = DistributionStats.getStatTime();
    stats.incProcessCloseCqTime(start - oldStart);
  }

}

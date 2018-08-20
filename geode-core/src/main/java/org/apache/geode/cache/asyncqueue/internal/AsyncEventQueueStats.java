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
package org.apache.geode.cache.asyncqueue.internal;

import org.apache.geode.internal.cache.wan.GatewaySenderStatsImpl;
import org.apache.geode.statistics.StatisticDescriptor;
import org.apache.geode.statistics.StatisticsFactory;
import org.apache.geode.statistics.StatisticsType;


public class AsyncEventQueueStats extends GatewaySenderStatsImpl {

  public static final String typeName = "AsyncEventQueueStatistics";

  /**
   * The <code>StatisticsType</code> of the statistics
   */
  private StatisticsType type;

  /**
   * Returns the internal ID for {@link #getEventQueueSize()} statistic
   */
  public static String getEventQueueSizeId() {
    return GatewaySenderStatsImpl.EVENT_QUEUE_SIZE;
  }

  /**
   * Returns the internal ID for {@link #getTempEventQueueSize()} statistic
   */
  public static String getEventTempQueueSizeId() {
    return GatewaySenderStatsImpl.TMP_EVENT_QUEUE_SIZE;
  }

  @Override
  public void initializeStats(StatisticsFactory factory) {
    type = factory.createType(typeName, "Stats for activity in the AsyncEventQueue",
        new StatisticDescriptor[] {
            factory.createIntCounter(EVENTS_RECEIVED, "Number of events received by this queue.",
                "operations"),
            factory.createIntCounter(EVENTS_QUEUED, "Number of events added to the event queue.",
                "operations"),
            factory.createLongCounter(EVENT_QUEUE_TIME, "Total time spent queueing events.",
                "nanoseconds"),
            factory.createIntGauge(EVENT_QUEUE_SIZE, "Size of the event queue.", "operations",
                false),
            factory.createIntGauge(SECONDARY_EVENT_QUEUE_SIZE, "Size of the secondary event queue.",
                "operations", false),
            factory.createIntGauge(EVENTS_PROCESSED_BY_PQRM,
                "Total number of events processed by Parallel Queue Removal Message(PQRM).",
                "operations", false),
            factory.createIntGauge(TMP_EVENT_QUEUE_SIZE, "Size of the temporary events queue.",
                "operations", false),
            factory.createIntCounter(EVENTS_NOT_QUEUED_CONFLATED,
                "Number of events received but not added to the event queue because the queue already contains an event with the event's key.",
                "operations"),
            factory.createIntCounter(EVENTS_CONFLATED_FROM_BATCHES,
                "Number of events conflated from batches.", "operations"),
            factory.createIntCounter(EVENTS_DISTRIBUTED,
                "Number of events removed from the event queue and sent.", "operations"),
            factory.createIntCounter(EVENTS_EXCEEDING_ALERT_THRESHOLD,
                "Number of events exceeding the alert threshold.", "operations", false),
            factory.createLongCounter(BATCH_DISTRIBUTION_TIME,
                "Total time spent distributing batches of events to receivers.", "nanoseconds"),
            factory.createIntCounter(BATCHES_DISTRIBUTED,
                "Number of batches of events removed from the event queue and sent.", "operations"),
            factory.createIntCounter(BATCHES_REDISTRIBUTED,
                "Number of batches of events removed from the event queue and resent.",
                "operations", false),
            factory.createIntCounter(UNPROCESSED_TOKENS_ADDED_BY_PRIMARY,
                "Number of tokens added to the secondary's unprocessed token map by the primary (though a listener).",
                "tokens"),
            factory.createIntCounter(UNPROCESSED_EVENTS_ADDED_BY_SECONDARY,
                "Number of events added to the secondary's unprocessed event map by the secondary.",
                "events"),
            factory.createIntCounter(UNPROCESSED_EVENTS_REMOVED_BY_PRIMARY,
                "Number of events removed from the secondary's unprocessed event map by the primary (though a listener).",
                "events"),
            factory.createIntCounter(UNPROCESSED_TOKENS_REMOVED_BY_SECONDARY,
                "Number of tokens removed from the secondary's unprocessed token map by the secondary.",
                "tokens"),
            factory.createIntCounter(UNPROCESSED_EVENTS_REMOVED_BY_TIMEOUT,
                "Number of events removed from the secondary's unprocessed event map by a timeout.",
                "events"),
            factory.createIntCounter(UNPROCESSED_TOKENS_REMOVED_BY_TIMEOUT,
                "Number of tokens removed from the secondary's unprocessed token map by a timeout.",
                "tokens"),
            factory.createIntGauge(UNPROCESSED_EVENT_MAP_SIZE,
                "Current number of entries in the secondary's unprocessed event map.", "events",
                false),
            factory.createIntGauge(UNPROCESSED_TOKEN_MAP_SIZE,
                "Current number of entries in the secondary's unprocessed token map.", "tokens",
                false),
            factory.createIntGauge(CONFLATION_INDEXES_MAP_SIZE,
                "Current number of entries in the conflation indexes map.", "events"),
            factory.createIntCounter(NOT_QUEUED_EVENTS, "Number of events not added to queue.",
                "events"),
            factory.createIntCounter(EVENTS_DROPPED_DUE_TO_PRIMARY_SENDER_NOT_RUNNING,
                "Number of events dropped because the primary gateway sender is not running.",
                "events"),
            factory.createIntCounter(EVENTS_FILTERED,
                "Number of events filtered through GatewayEventFilter.", "events"),
            factory.createIntCounter(LOAD_BALANCES_COMPLETED, "Number of load balances completed",
                "operations"),
            factory.createIntGauge(LOAD_BALANCES_IN_PROGRESS, "Number of load balances in progress",
                "operations"),
            factory.createLongCounter(LOAD_BALANCE_TIME,
                "Total time spent load balancing this sender",
                "nanoseconds"),
            factory.createIntCounter(SYNCHRONIZATION_EVENTS_ENQUEUED,
                "Number of synchronization events added to the event queue.", "operations"),
            factory.createIntCounter(SYNCHRONIZATION_EVENTS_PROVIDED,
                "Number of synchronization events provided to other members.", "operations"),});

    // Initialize id fields
    eventsReceivedId = type.nameToId(EVENTS_RECEIVED);
    eventsQueuedId = type.nameToId(EVENTS_QUEUED);
    eventsNotQueuedConflatedId = type.nameToId(EVENTS_NOT_QUEUED_CONFLATED);
    eventQueueTimeId = type.nameToId(EVENT_QUEUE_TIME);
    eventQueueSizeId = type.nameToId(EVENT_QUEUE_SIZE);
    secondaryEventQueueSizeId = type.nameToId(SECONDARY_EVENT_QUEUE_SIZE);
    eventsProcessedByPQRMId = type.nameToId(EVENTS_PROCESSED_BY_PQRM);
    eventTmpQueueSizeId = type.nameToId(TMP_EVENT_QUEUE_SIZE);
    eventsDistributedId = type.nameToId(EVENTS_DISTRIBUTED);
    eventsExceedingAlertThresholdId = type.nameToId(EVENTS_EXCEEDING_ALERT_THRESHOLD);
    batchDistributionTimeId = type.nameToId(BATCH_DISTRIBUTION_TIME);
    batchesDistributedId = type.nameToId(BATCHES_DISTRIBUTED);
    batchesRedistributedId = type.nameToId(BATCHES_REDISTRIBUTED);
    unprocessedTokensAddedByPrimaryId = type.nameToId(UNPROCESSED_TOKENS_ADDED_BY_PRIMARY);
    unprocessedEventsAddedBySecondaryId = type.nameToId(UNPROCESSED_EVENTS_ADDED_BY_SECONDARY);
    unprocessedEventsRemovedByPrimaryId = type.nameToId(UNPROCESSED_EVENTS_REMOVED_BY_PRIMARY);
    unprocessedTokensRemovedBySecondaryId = type.nameToId(UNPROCESSED_TOKENS_REMOVED_BY_SECONDARY);
    unprocessedEventsRemovedByTimeoutId = type.nameToId(UNPROCESSED_EVENTS_REMOVED_BY_TIMEOUT);
    unprocessedTokensRemovedByTimeoutId = type.nameToId(UNPROCESSED_TOKENS_REMOVED_BY_TIMEOUT);
    unprocessedEventMapSizeId = type.nameToId(UNPROCESSED_EVENT_MAP_SIZE);
    unprocessedTokenMapSizeId = type.nameToId(UNPROCESSED_TOKEN_MAP_SIZE);
    conflationIndexesMapSizeId = type.nameToId(CONFLATION_INDEXES_MAP_SIZE);
    notQueuedEventsId = type.nameToId(NOT_QUEUED_EVENTS);
    eventsDroppedDueToPrimarySenderNotRunningId =
        type.nameToId(EVENTS_DROPPED_DUE_TO_PRIMARY_SENDER_NOT_RUNNING);
    eventsFilteredId = type.nameToId(EVENTS_FILTERED);
    eventsConflatedFromBatchesId = type.nameToId(EVENTS_CONFLATED_FROM_BATCHES);
    loadBalancesCompletedId = type.nameToId(LOAD_BALANCES_COMPLETED);
    loadBalancesInProgressId = type.nameToId(LOAD_BALANCES_IN_PROGRESS);
    loadBalanceTimeId = type.nameToId(LOAD_BALANCE_TIME);
    synchronizationEventsEnqueuedId = type.nameToId(SYNCHRONIZATION_EVENTS_ENQUEUED);
    synchronizationEventsProvidedId = type.nameToId(SYNCHRONIZATION_EVENTS_PROVIDED);
  }

  /**
   * Constructor.
   *
   * @param factory The <code>StatisticsFactory</code> which creates the <code>Statistics</code>
   *        instance
   * @param asyncQueueId The id of the <code>AsyncEventQueue</code> used to generate the name of
   *        the
   *        <code>Statistics</code>
   */
  public AsyncEventQueueStats(StatisticsFactory factory, String asyncQueueId) {
    super();
    initializeStats(factory);
    this.stats = factory.createAtomicStatistics(type, "asyncEventQueueStats-" + asyncQueueId);
  }

  public StatisticsType getType() {
    return type;
  }
}

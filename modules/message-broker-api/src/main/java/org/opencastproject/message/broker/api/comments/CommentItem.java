/**
 * Licensed to The Apereo Foundation under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 *
 * The Apereo Foundation licenses this file to you under the Educational
 * Community License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at:
 *
 *   http://opensource.org/licenses/ecl2.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.opencastproject.message.broker.api.comments;

import org.opencastproject.message.broker.api.MessageItem;

import java.io.Serializable;

/**
 * {@link Serializable} class that represents all of the possible messages sent through a comment queue.
 */
public class CommentItem implements MessageItem, Serializable {

  private static final long serialVersionUID = -3946543513879987169L;

  public static final String COMMENT_QUEUE_PREFIX = "COMMENT.";

  public static final String COMMENT_QUEUE = COMMENT_QUEUE_PREFIX + "QUEUE";

  private final String eventId;
  private final boolean hasComments;
  private final boolean hasOpenComments;
  private final boolean needsCutting;
  private final Type type;

  public enum Type {
    Update
  };

  /**
   * @param eventId
   *          The event to update
   * @param hasComments
   *          Whether the event has comments
   * @param hasOpenComments
   *          Whether the event has open comments
   * @param needsCutting
   *          Whether the event has an open comment that it needs cutting
   * @return Builds a {@link CommentItem} for updating a comment.
   */
  public static CommentItem update(String eventId, boolean hasComments, boolean hasOpenComments, boolean needsCutting) {
    return new CommentItem(eventId, hasComments, hasOpenComments, needsCutting);
  }

  /**
   * Constructor to build an Update {@link CommentItem}
   */
  public CommentItem(String eventId, boolean hasComments, boolean hasOpenComments, boolean needsCutting) {
    this.eventId = eventId;
    this.hasComments = hasComments;
    this.hasOpenComments = hasOpenComments;
    this.needsCutting = needsCutting;
    this.type = Type.Update;
  }

  @Override
  public String getId() {
    return eventId;
  }

  public String getEventId() {
    return eventId;
  }

  public boolean hasComments() {
    return hasComments;
  }

  public boolean hasOpenComments() {
    return hasOpenComments;
  }

  public boolean needsCutting() {
    return needsCutting;
  }

  public Type getType() {
    return type;
  }

}

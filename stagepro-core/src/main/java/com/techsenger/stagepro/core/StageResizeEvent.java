/*
 * Copyright 2024 Pavel Castornii.
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

package com.techsenger.stagepro.core;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Pavel Castornii
 */
public class StageResizeEvent extends Event {

    /**
     * Common supertype for all stage resize event types.
     */
    public static final EventType<StageResizeEvent> ANY = new EventType<>(Event.ANY, "STAGE_RESIZE");

    /**
     * This event occurs when user starts resizing a stage.
     */
    public static final EventType<StageResizeEvent> STAGE_RESIZING_STARTED =
            new EventType<>(StageResizeEvent.ANY, "STAGE_RESIZING_STARTED");

    /**
     * This event occurs when user finishes resizing a stage.
     */
    public static final EventType<StageResizeEvent> STAGE_RESIZING_FINISHED =
            new EventType<>(StageResizeEvent.ANY, "STAGE_RESIZING_FINISHED");

    private final MouseEvent mouseEvent;

    public StageResizeEvent(EventType<? extends StageResizeEvent> eventType, MouseEvent mouseEvent) {
        super(eventType);
        this.mouseEvent = mouseEvent;
    }

    public MouseEvent getMouseEvent() {
        return mouseEvent;
    }
}

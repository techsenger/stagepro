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

import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 *
 * @author Pavel Castornii
 */
class StageResizer extends AbstractResizer {

    private Stage stage;

    private final EventHandler<MouseEvent> mousedPressedHandler = (event) -> mousePressed(event);

    private final EventHandler<MouseEvent> mousedMovedHandler = (event) -> mouseMoved(event);

    private final EventHandler<MouseEvent> mousedDraggedHandler = (event) -> mouseDragged(event);

    private final EventHandler<MouseEvent> mousedReleasedHandler = (event) -> mouseReleased(event);

    StageResizer(DoubleProperty minWidth, DoubleProperty minHeight, DoubleProperty maxWidth,
            DoubleProperty maxHeight, EventHandler<? super MouseEvent> startedHandler,
            EventHandler<? super MouseEvent> finishedHandler) {
        super(minWidth, minHeight, maxWidth, maxHeight, startedHandler, finishedHandler);
    }

    /**
     * Initializes resizer for concrete region.
     *
     * @param stage
     */
    public void initialize(Stage stage) {
        this.stage = stage;
        this.stage.addEventFilter(MouseEvent.MOUSE_PRESSED, mousedPressedHandler);
        this.stage.addEventFilter(MouseEvent.MOUSE_MOVED, mousedMovedHandler);
        this.stage.addEventFilter(MouseEvent.MOUSE_DRAGGED, mousedDraggedHandler);
        this.stage.addEventFilter(MouseEvent.MOUSE_RELEASED, mousedReleasedHandler);
    }

    /**
     * Deinitializes resizer by removing all its handlers from stage.
     */
    public void deinitialize() {
        this.stage.removeEventFilter(MouseEvent.MOUSE_PRESSED, mousedPressedHandler);
        this.stage.removeEventFilter(MouseEvent.MOUSE_MOVED, mousedMovedHandler);
        this.stage.removeEventFilter(MouseEvent.MOUSE_DRAGGED, mousedDraggedHandler);
        this.stage.removeEventFilter(MouseEvent.MOUSE_RELEASED, mousedReleasedHandler);
        this.stage = null;
    }

    @Override
    protected double getWidth() {
        return this.stage.getWidth();
    }

    @Override
    protected void setWidth(double width) {
        this.stage.setWidth(width);
    }

    @Override
    protected double getHeight() {
        return this.stage.getHeight();
    }

    @Override
    protected void setHeight(double height) {
        this.stage.setHeight(height);
    }

    @Override
    protected void setCursor(Cursor cursor) {
        this.stage.getScene().setCursor(cursor);
    }

    @Override
    protected double getX() {
        return this.stage.getX();
    }

    @Override
    protected void setX(double x) {
        this.stage.setX(x);
    }

    @Override
    protected double getY() {
        return this.stage.getY();
    }

    @Override
    protected void setY(double y) {
        this.stage.setY(y);
    }

    @Override
    protected double getMouseX(MouseEvent event) {
        return event.getScreenX();
    }

    @Override
    protected double getMouseY(MouseEvent event) {
        return event.getScreenY();
    }

    @Override
    protected double getMargin() {
        return 4;
    }

    protected Stage getStage() {
        return stage;
    }
}

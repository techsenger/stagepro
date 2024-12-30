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

import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

/**
 * This class lets create resizers that can work with different JavaFX components, for example, Region,
 * Stage (without borders) etc.
 *
 * @author Pavel Castornii
 */
abstract class AbstractResizer {

    private double pressedY;

    private double pressedX;

    private double pressedWidth;

    private double pressedHeight;

    private double pressedMouseY;

    private double pressedMouseX;

    private Consumer<MouseEvent> helper;

    private final DoubleProperty minWidth;

    private final DoubleProperty minHeight;

    private final DoubleProperty maxWidth;

    private final DoubleProperty maxHeight;

    private boolean cursorReplaced = false;

    private final EventHandler<? super MouseEvent> startedHandler;

    private final EventHandler<? super MouseEvent> finishedHandler;

    private final BooleanProperty disabled = new SimpleBooleanProperty(false);

    AbstractResizer(DoubleProperty minWidth, DoubleProperty minHeight, DoubleProperty maxWidth,
            DoubleProperty maxHeight, EventHandler<? super MouseEvent> startedHandler,
            EventHandler<? super MouseEvent> finishedHandler) {
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.startedHandler = startedHandler;
        this.finishedHandler = finishedHandler;
    }

    public BooleanProperty disabledProperty() {
        return disabled;
    }

    public boolean isDisabled() {
        return this.disabled.get();
    }

    public void setDisabled(boolean disabled) {
        this.disabled.set(disabled);
    }

    protected abstract double getWidth();

    protected abstract void setWidth(double width);

    protected abstract double getHeight();

    protected abstract void setHeight(double height);

    protected abstract void setCursor(Cursor cursor);

    protected abstract double getX();

    protected abstract void setX(double x);

    protected abstract double getY();

    protected abstract void setY(double y);

    protected abstract double getMouseX(MouseEvent event);

    protected abstract double getMouseY(MouseEvent event);

    protected abstract double getMargin();

    protected void mouseReleased(MouseEvent event) {
        if (this.helper != null) {
            this.helper = null;
            setCursor(Cursor.DEFAULT);
            this.cursorReplaced = false;
            if (this.finishedHandler != null) {
                this.finishedHandler.handle(event);
            }
            event.consume();
        }
    }

    protected void mouseMoved(MouseEvent event) {
        if (this.disabled.get()) {
            return;
        }
        var cursor = resolveCursorAndHelper(event, false);
        if (cursor != null || this.helper != null) {
            if (cursor != null) {
                setCursor(cursor);
                this.cursorReplaced = true;
                event.consume();
            }
        } else {
            if (this.cursorReplaced) {
                setCursor(Cursor.DEFAULT);
                this.cursorReplaced = false;
                event.consume();
            }
        }
    }

    protected void mouseDragged(MouseEvent event) {
        if (this.helper != null) {
            this.helper.accept(event);
            event.consume();
        }
    }

    protected void mousePressed(MouseEvent event) {
        if (this.disabled.get()) {
            return;
        }
        var cursor = resolveCursorAndHelper(event, true);
        if (cursor == null) {
            return;
        }
        this.pressedMouseX = getMouseX(event);
        this.pressedMouseY = getMouseY(event);
        this.pressedHeight = getHeight();
        this.pressedWidth = getWidth();
        this.pressedX = getX();
        this.pressedY = getY();
        if (this.startedHandler != null) {
            this.startedHandler.handle(event);
        }
        event.consume();
    }

    /**
     * Returns cursor or null if mouse outside dragging zone.
     *
     * @param event
     * @return
     */
    private Cursor resolveCursorAndHelper(MouseEvent event, boolean setHelper) {
        //when shadow is added, then mouse events are fired on this shadow
        if ((event.getX() < 0 || event.getX() > getWidth()) || (event.getY() < 0 || event.getY() > getHeight())) {
            return null;
        }

        if (event.getY() <= getMargin()) {
            if (event.getX() <= getMargin()) {
                if (setHelper) {
                    this.helper = (e) -> {
                        this.resizeNorth(e);
                        this.resizeWest(e);
                    };
                }
                return Cursor.NW_RESIZE;
            } else if (event.getX() >= (getWidth() - getMargin())) {
                if (setHelper) {
                    this.helper = (e) -> {
                        this.resizeNorth(e);
                        this.resizeEast(e);
                    };
                }
                return Cursor.NE_RESIZE;
            }
            if (setHelper) {
                this.helper = (e) -> {
                    this.resizeNorth(e);
                };
            }
            return Cursor.N_RESIZE;
        } else if (event.getY() >= (getHeight() - getMargin())) {
            if (event.getX() <= getMargin()) {
                if (setHelper) {
                    this.helper = (e) -> {
                        this.resizeSouth(e);
                        this.resizeWest(e);
                    };
                }
                return Cursor.SW_RESIZE;
            } else if (event.getX() >= (getWidth() - getMargin())) {
                if (setHelper) {
                    this.helper = (e) -> {
                        this.resizeSouth(e);
                        this.resizeEast(e);
                    };
                }
                return Cursor.SE_RESIZE;
            }
            if (setHelper) {
                this.helper = (e) -> {
                    this.resizeSouth(e);
                };
            }
            return Cursor.S_RESIZE;
        }
        if (event.getX() <= getMargin()) {
            if (setHelper) {
                this.helper = (e) -> {
                    this.resizeWest(e);
                };
            }
            return Cursor.W_RESIZE;
        } else if (event.getX() >= getWidth() - getMargin()) {
            if (setHelper) {
                    this.helper = (e) -> {
                    this.resizeEast(e);
                };
            }
            return Cursor.E_RESIZE;
        }
        return null;
    }

    private void resizeNorth(MouseEvent e) {
        double mouseY = getMouseY(e);
        var diff = this.pressedMouseY - mouseY;
        double newHeight = this.pressedHeight + diff;
        if (newHeight >= this.minHeight.get() && newHeight <= resolveMaxHeight()) {
            //at the same time we need to update y position of the region
            var newY = this.pressedY - diff;
            setHeight(newHeight);
            setY(newY);
        }
    }

    private void resizeEast(MouseEvent e) {
        double mouseX = getMouseX(e);
        var diff = mouseX - this.pressedMouseX;
        double newWidth = this.pressedWidth + diff;
        if (newWidth >= this.minWidth.get() && newWidth <= resolveMaxWidth()) {
            setWidth(newWidth);
        }
    }

    private void resizeSouth(MouseEvent e) {
        double mouseY = getMouseY(e);
        var diff = mouseY - this.pressedMouseY;
        double newHeight = this.pressedHeight + diff;
        if (newHeight >= this.minHeight.get() && newHeight <= resolveMaxHeight()) {
            setHeight(newHeight);
        }
    }

    private void resizeWest(MouseEvent e) {
        double mouseX = getMouseX(e);
        var diff = this.pressedMouseX - mouseX;
        double newWidth = this.pressedWidth + diff;
        if (newWidth >= this.minWidth.get() && newWidth <= resolveMaxWidth()) {
            //at the same time we need to update x position of the region
            var newX = this.pressedX - diff;
            setWidth(newWidth);
            setX(newX);
        }
    }

    private double resolveMaxWidth() {
        //checking if max width is not zero
        if (this.maxWidth.get() > 0.1) {
            return this.maxWidth.get();
        } else {
            //no limit
            return Double.MAX_VALUE;
        }
    }

    private double resolveMaxHeight() {
        //checking if max height is not zero
        if (this.maxHeight.get() > 0.1) {
            return this.maxHeight.get();
        } else {
            //no limit
            return Double.MAX_VALUE;
        }
    }

}

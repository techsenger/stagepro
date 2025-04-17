/*
 * Copyright 2024-2025 Pavel Castornii.
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

package com.techsenger.stagepro.sampler;

import com.techsenger.stagepro.core.BaseStageController;
import com.techsenger.stagepro.core.MaximizeButton.ResizableStatePolicy;
import com.techsenger.stagepro.core.SimpleStageController;
import com.techsenger.stagepro.core.StageResizeEvent;
import com.techsenger.stagepro.core.StandardStageController;
import com.techsenger.toolkit.fx.Spacer;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Pavel Castornii
 */
public class Sampler extends Application {

    private static final double SAMPLE_STAGE_WIDTH = 600;

    private static final double SAMPLE_STAGE_HEIGHT = 400;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ObservableList<Sample> samples = FXCollections.observableArrayList(createSamples());
        TableView<Sample> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setItems(samples);

        TableColumn<Sample, Integer> idColumn = new TableColumn<>("Id");
        idColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        idColumn.setMaxWidth(50);
        idColumn.setMinWidth(50);
        idColumn.setResizable(false);

        TableColumn<Sample, String> controllerColumn = new TableColumn<>("Controller");
        controllerColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getController()));
        controllerColumn.setMaxWidth(250);
        controllerColumn.setMinWidth(250);
        controllerColumn.setResizable(false);

        TableColumn<Sample, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDescription()));

        TableColumn<Sample, Runnable> actionColumn = new TableColumn<>("Action");
        actionColumn.getStyleClass().add("action");
        actionColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getAction()));
        actionColumn.setMaxWidth(100);
        actionColumn.setMinWidth(100);
        actionColumn.setResizable(false);
        actionColumn.setCellFactory(column -> {
            TableCell<Sample, Runnable> cell = new TableCell<>() {

                private final Button button = new Button("Run");

                {
                    button.setOnAction(event -> {
                        Sample sample = getTableRow().getItem();
                        if (sample != null) {
                            sample.getAction().run();
                        }
                    });
                    button.setPadding(new Insets(3, 6, 3, 6));
                    setPadding(new Insets(2));
                }

                @Override
                protected void updateItem(Runnable sample, boolean empty) {
                    super.updateItem(sample, empty);
                    if (sample == null || empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(button);
                    }
                }
            };
            return cell;
        });

        tableView.getColumns().addAll(idColumn, controllerColumn, descriptionColumn, actionColumn);

        VBox root = new VBox(tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        Scene scene = new Scene(root, 1000, 400);
        scene.getStylesheets().add(Sampler.class.getResource("sampler.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("StagePro Sampler");
        primaryStage.show();
    }

    private List<Sample> createSamples() {
        return List.of(
                createSample1(),
                createSample2(),
                createSample3(),
                createSample4(),
                createSample5()
        );
    }

    private Sample createSample1() {
        return new Sample(1, BaseStageController.class.getSimpleName(), "Empty title bar", () -> {
            var stage = new Stage();
            var controller = new BaseStageController(stage, SAMPLE_STAGE_WIDTH, SAMPLE_STAGE_HEIGHT);
            setStylesheet(controller);
            setContent(controller);
            stage.show();
        });
    }

    private Sample createSample2() {
        return new Sample(2, SimpleStageController.class.getSimpleName(),
                "Icon and text on the left, close button on the right", () -> {
            var stage = new Stage();
            var controller = new SimpleStageController(stage, SAMPLE_STAGE_WIDTH, SAMPLE_STAGE_HEIGHT);
            setTitle(controller);
            setStylesheet(controller);
            setContent(controller);
            stage.show();
        });
    }

    private Sample createSample3() {
        return new Sample(3, StandardStageController.class.getSimpleName(),
                "Icon and text on the left, three buttons on the right", () -> {
            var stage = new Stage();
            var controller = new StandardStageController(stage, SAMPLE_STAGE_WIDTH, SAMPLE_STAGE_HEIGHT);
            setTitle(controller);
            setStylesheet(controller);
            setContent(controller);
            stage.show();
        });
    }

    private Sample createSample4() {
        class LeftStandardStageController extends StandardStageController {

            LeftStandardStageController(Stage stage, double width, double height) {
                super(stage, width, height, false);
                getButtonBox().getChildren().addAll(getCloseButton(), getMinimizeButton(), getMaximizeButton());
                getTitleBar().getChildren().addAll(getButtonBox(), new Spacer(10.0), getTitleLabel(),
                        new Spacer());
            }
        }
        return new Sample(4, LeftStandardStageController.class.getSimpleName(),
                "Three buttons, text on the left", () -> {
            var stage = new Stage();
            var controller = new LeftStandardStageController(stage, SAMPLE_STAGE_WIDTH, SAMPLE_STAGE_HEIGHT);
            setTitle(controller);
            setStylesheet(controller);
            setContent(controller);
            stage.show();
        });
    }

    private Sample createSample5() {
        return new Sample(5, StandardStageController.class.getSimpleName(),
                "Icon and menu on the left, three buttons on the right", () -> {
            var stage = new Stage();
            class LeftStandardStageController extends StandardStageController {

                private final Menu fileMenu = new Menu("_File");

                private final Menu editMenu = new Menu("_Edit");

                private final Menu helptMenu = new Menu("_Help");

                private final MenuBar menuBar = new MenuBar(fileMenu, editMenu, helptMenu);

                LeftStandardStageController(Stage stage, double width, double height) {
                    super(stage, width, height, false);
                    getButtonBox().getChildren().addAll(getMinimizeButton(), getMaximizeButton(), getCloseButton());
                    getTitleBar().getChildren().addAll(getIconView(), menuBar, new Spacer(), getButtonBox());
                }
            }
            var controller = new LeftStandardStageController(stage, SAMPLE_STAGE_WIDTH, SAMPLE_STAGE_HEIGHT);
            setTitle(controller);
            setStylesheet(controller);
            setContent(controller);
            stage.show();
        });
    }

    private void setTitle(SimpleStageController controller) {
        controller.getTitleLabel().setText("Title");

    }

    private void setStylesheet(BaseStageController controller) {
        controller.getStage().getScene().getStylesheets().add(Sampler.class.getResource("sample.css").toExternalForm());
    }

    private void setContent(BaseStageController controller) {
        var stage = controller.getStage();
        var gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        var rowIndex = 0;
        if (controller.getClass() == BaseStageController.class) {
            var button = new Button("Close");
            button.setMaxWidth(Double.MAX_VALUE);
            button.setOnAction(e -> stage.close());
            GridPane.setHgrow(button, Priority.ALWAYS);
            GridPane.setColumnSpan(button, 2);
            gridPane.add(button, 0, rowIndex);
            rowIndex++;
        }
        if (controller instanceof StandardStageController) {
            gridPane.add(new Label("Max Button Policy"), 0, rowIndex);
            var polcies = FXCollections.observableArrayList(ResizableStatePolicy.VISIBILITY,
                    ResizableStatePolicy.INTERACTIVITY);
            var policyComboBox = new ComboBox<ResizableStatePolicy>(polcies);
            var maxButton = ((StandardStageController) controller).getMaximizeButton();
            policyComboBox.valueProperty().bindBidirectional(maxButton.policyProperty());
            gridPane.add(policyComboBox, 1, rowIndex);
            rowIndex++;
        }

        var resizableCheckBox = new CheckBox("Resizable");
        resizableCheckBox.selectedProperty().bindBidirectional(stage.resizableProperty());
        gridPane.add(resizableCheckBox, 0, rowIndex);
        var darkThemeCheckBox = new CheckBox("Dark Theme");
        darkThemeCheckBox.selectedProperty().addListener((ov, oldV, newV) -> {
            if (newV) {
                controller.getStage().getScene().getRoot().getStyleClass().add("dark");
            } else {
                controller.getStage().getScene().getRoot().getStyleClass().remove("dark");
            }
        });
        gridPane.add(darkThemeCheckBox, 1, rowIndex);

        rowIndex++;
        EventHandler<StageResizeEvent> started = e -> System.out.println("Resize started");
        EventHandler<StageResizeEvent> finished = e -> System.out.println("Resize finished");
        var resizeHandlersCheckBox = new CheckBox("Resize Handlers");
        resizeHandlersCheckBox.selectedProperty().addListener((ov, oldV, newV) -> {
            if (newV) {
                stage.addEventHandler(StageResizeEvent.STAGE_RESIZE_STARTED, started);
                stage.addEventHandler(StageResizeEvent.STAGE_RESIZE_FINISHED, finished);
            } else {
                stage.removeEventHandler(StageResizeEvent.STAGE_RESIZE_STARTED, started);
                stage.removeEventHandler(StageResizeEvent.STAGE_RESIZE_FINISHED, finished);
            }
        });
        gridPane.add(resizeHandlersCheckBox, 0, rowIndex);

        var content = new BorderPane();
        content.setCenter(gridPane);
        controller.setContent(content);
    }

}

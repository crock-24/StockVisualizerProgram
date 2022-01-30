// IMPORTS
// These are some classes that may be useful for completing the project.
// You may have to add others.
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;
import javafx.stage.Stage;
import javafx.concurrent.Worker.State;
import javafx.concurrent.Worker;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * The main class for Week5InClassProgram. Week5InClassProgram constructs the JavaFX window and
 * handles interactions with the dynamic components contained therein.
 */
public class Week5InClassProgram extends Application {
    // INSTANCE VARIABLES
    // These variables are included to get you started.
    private Stage stage = null;
    private BorderPane borderPane = null;
    private WebView view = null;
    private WebEngine webEngine = null;
    private TextField statusbar = null;
    private TextField addressbar = null;
    private Button backbutton = null;
    private Button forwardbutton = null;
    private Button helpbutton = null;
    private String helploc = "File:C:\\Users\\August\\Desktop\\School\\CS 1122\\Week5InClassProgram\\help.html";

    // HELPER METHODS
    /**
     * Retrieves the value of a command line argument specified by the index.
     *
     * @param index - position of the argument in the args list.
     * @return The value of the command line argument.
     */
    private String getParameter( int index ) {
        Parameters params = getParameters();
        List<String> parameters = params.getRaw();
        return !parameters.isEmpty() ? parameters.get(index) : "";
    }

    /**
     * Creates a WebView which handles mouse and some keyboard events, and
     * manages scrolling automatically, so there's no need to put it into a ScrollPane.
     * The associated WebEngine is created automatically at construction time.
     *
     * @return browser - a WebView container for the WebEngine.
     */
    private WebView makeHtmlView( ) {
        view = new WebView();
        webEngine = view.getEngine();
        return view;
    }

    /**
     * Generates the status bar layout and text field.
     *
     * @return statusbarPane - the HBox layout that contains the statusbar.
     */
    private HBox makeStatusBar( ) {
        HBox statusbarPane = new HBox();
        statusbarPane.setPadding(new Insets(5, 4, 5, 4));
        statusbarPane.setSpacing(10);
        statusbarPane.setStyle("-fx-background-color: #336699;");
        statusbar = new TextField();
        HBox.setHgrow(statusbar, Priority.ALWAYS);
        statusbarPane.getChildren().addAll(statusbar);
        return statusbarPane;
    }
    /**
     * Generates the status bar layout and text field.
     *
     * @return addressbarPane - the Hbox layout that contains the adressbar, forward, backward and help buttons
     */
    private Pane makeAddressBar( ) {
        HBox addressbarPane = new HBox();
        addressbarPane.setPadding(new Insets(5, 4, 5, 4));
        addressbarPane.setSpacing(5);
        addressbarPane.setStyle("-fx-background-color: #403d3e;");
        addressbar = new TextField();
        backbutton = new Button("<-");
        forwardbutton = new Button("->");
        helpbutton = new Button("?");
        HBox.setHgrow(addressbar, Priority.ALWAYS);

        addressbarPane.getChildren().addAll(backbutton,forwardbutton,addressbar,helpbutton);
        return addressbarPane;
    }
    /** Loads the address bar url into the web viewer and calls set title
     */

    private void backClicked() throws IndexOutOfBoundsException{
        backbutton.setOnAction(
                (event) ->
                {
                    try{
                        webEngine.getHistory().go(-1);

                    }
                    catch (IndexOutOfBoundsException e){
                        //no history position
                    }

                });
    }
    private void forwardClicked() throws IndexOutOfBoundsException{
        forwardbutton.setOnAction(
                (event) ->
                {
                    try{
                        webEngine.getHistory().go(1);

                    }
                    catch (IndexOutOfBoundsException e){
                        //no history position
                    }
                });
    }
    private void helpClicked(){
        helpbutton.setOnAction(
                (event) ->
                {
                    webEngine.load(helploc);
                });
    }
    private void Searched(){
        addressbar.setOnKeyPressed(
                (e -> {
                    if (e.getCode().equals(KeyCode.ENTER)){
                        webEngine.load(addressbar.getText());
                    }
                })
        );
    }
    private void AddressChanged(){
        webEngine.getLoadWorker().stateProperty().addListener(
                new ChangeListener<State>() {
                    @Override
                    public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
                        if (newValue == State.SUCCEEDED){

                            if (webEngine.getTitle()==null){
                                stage.setTitle(webEngine.getLocation());
                            }
                            else{
                                stage.setTitle(webEngine.getTitle());
                            }
                            addressbar.setText(webEngine.getLocation());
                        }
                    }
                }
        );
    }
    private void hoverLink(){
        webEngine.setOnStatusChanged(e ->{
            statusbar.setText(e.getData());
        });
    }
    private void HandleEvents(){
        forwardClicked();
        backClicked();
        helpClicked();
        AddressChanged();
        hoverLink();
        Searched();

    }
    private String getUrlTitle(String s){
        return webEngine.getTitle();
    }
    /**intitializes the stage
     */
    private Stage loadStage(Stage stage){

        borderPane = new BorderPane();
        Scene scene = new Scene(borderPane,800,600);
        borderPane.setCenter(makeHtmlView());
        borderPane.setBottom(makeStatusBar());
        borderPane.setTop(makeAddressBar());
        stage.setScene(scene);
        stage.setTitle(addressbar.getText());

        if(!getParameter(0).equals("")){
            webEngine.load(getParameter(0));
        }
        else{
            webEngine.load(helploc);
        }


        return stage;



    }
    /**Searches
     @param stage
     */


    // REQUIRED METHODS
    /**
     * The main entry point for all JavaFX applications. The start method is
     * called after the init method has returned, and after the system is ready
     * for the application to begin running.
     *
     * NOTE: This method is called on the JavaFX Application Thread.
     *
     * @param - the primary stage for this application, onto which
     * the application scene can be set.
     */
    @Override
    public void start(Stage stage) {
        this.stage = loadStage(stage);;
        HandleEvents();
        stage.show();
    }

    /**
     * The main( ) method is ignored in JavaFX applications.
     * main( ) serves only as fallback in case the application is launched
     * as a regular Java application, e.g., in IDEs with limited FX
     * support.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        launch(args);
    }
}

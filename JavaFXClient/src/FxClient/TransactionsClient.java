package FxClient;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.ClientConfig;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import financialApp.Transactionn;

public class TransactionsClient extends Application {
	
		private static URI getBaseURI() {
			return UriBuilder.fromUri("http://localhost:8080/RestServer/transaction/").build();
		}
	
		private static ClientConfig config = new ClientConfig();
		private static Client client = ClientBuilder.newClient(config);
		private static WebTarget service = client.target(getBaseURI());
	
 		public static void main(String[] args) {  
			launch(args); 
		}
		 
		@Override
		public void start(Stage primaryStage) throws Exception {

			//FlowPane root = new FlowPane();
			GridPane root = new GridPane();
			
			/* We will use the TableView component to list the books */
			
			TableView tableView = new TableView();

			/* Each of the columns must be linked to a colum in your model class (Book) - 
			 * Pay attention to the type
			 */		
	        TableColumn<Transactionn, String> column1 = new TableColumn<>("Descrição");
	        column1.setMinWidth(200);
	        column1.setCellValueFactory(new PropertyValueFactory<>("notes"));

	        TableColumn<Transactionn, String> column2 = new TableColumn<>("Tipo");
	        column2.setMinWidth(200);
	        column2.setCellValueFactory(new PropertyValueFactory<>("type"));
	        
	        TableColumn<Transactionn, String> column3 = new TableColumn<>("Data");
	        column3.setMinWidth(100);
	        column3.setCellValueFactory(new PropertyValueFactory<>("date"));
	        
	        TableColumn<Transactionn, Double> column4 = new TableColumn<>("Montante");
	        column4.setMinWidth(100);
	        column4.setCellValueFactory(new PropertyValueFactory<>("amount"));

	        /* Add the columns to the table view */
	        tableView.getColumns().add(column1);
	        tableView.getColumns().add(column2);
	        tableView.getColumns().add(column3);
	        tableView.getColumns().add(column4);
	        
	        // Load objects into table calling the REST service
	        fillTableView(tableView);

	        /*This command line gets the selected row and the corresponding Model Instance (Book)*/
	        TableView.TableViewSelectionModel<Transactionn> selectionModel = tableView.getSelectionModel();

	        /* You can choose between single and multiple selection*/
	        selectionModel.setSelectionMode(SelectionMode.SINGLE);
	        //selectionModel.setSelectionMode(SelectionMode.MULTIPLE);

	        /* Here is a list for the selected items in the table view */
	        ObservableList<Transactionn> selectedItems = selectionModel.getSelectedItems();

	        /* In case you need to check the selected item when it is changed */
	        selectedItems.addListener(new ListChangeListener<Transactionn>() {
	            @Override
	            public void onChanged(Change<? extends Transactionn> change) {
	                System.out.println("Selection changed: " + change.getList());
	            }
	        });

	        /* Select the first item of the table view */
	        selectionModel.select(0);

	        /* In case if you need to get the index of the table view */
	        ObservableList<Integer> selectedIndices = selectionModel.getSelectedIndices();
	        
	        //tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
	        //root.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
	        
	        // We bind the prefHeight- and prefWidthProperty to the height and width of the stage.
	        tableView.prefHeightProperty().bind(primaryStage.heightProperty());
	        tableView.prefWidthProperty().bind(primaryStage.widthProperty());
	        
	        /* 
	         * or we can use the scroll pane
	         * 
	        ScrollPane sp = new ScrollPane(tableView);
	        sp.setFitToHeight(true);
	        sp.setFitToWidth(true);
	        
	        */
	        
	        //root.getChildren().add(tableView);  
	        root.add(tableView, 0, 0, 4, 1);
			
	        Button btnNew = new Button("New");
			Button btnEdit = new Button("Edit");	
			Button btnCancel = new Button("Cancel");
			
			btnNew.setOnAction(ae -> { 
				System.out.println("New transaction data... ");
				showAddUpdateTransactionStage(primaryStage, new Transactionn()); 
				fillTableView(tableView);
	        });
							
			btnEdit.setOnAction(ae -> {
				System.out.println("Editing data... ");			
				showAddUpdateTransactionStage(primaryStage, selectedItems.get(0));
				fillTableView(tableView);
			});
			
//			btnDelete.setOnAction(ae -> {
//				System.out.println("Deleting data... ");				
//
//				if (showConfirmationDialog("Are you sure you want to delete the book? ")) {
//					deleteBook(selectedItems.get(0).getId());
//					fillTableView(tableView);
//				}			
//				
//			});
			
			btnCancel.setOnAction(ae -> {
				System.out.println("Cancelling...");
				fillTableView(tableView);
				primaryStage.close();
			});
			
					
			//root.getChildren().addAll(new Button[] { btnNew, btnEdit, btnDelete, btnCancel});
			root.add(btnNew, 0, 1, 1, 1);
			root.add(btnEdit, 1, 1, 1, 1);
			root.add(btnCancel, 3, 1, 1, 1);
			
			root.setHgap(5);
			root.setVgap(5);
							
			Scene scene = new Scene(root);
			primaryStage.setTitle("Simple Transactions CRUD example");
			primaryStage.setScene(scene);
			
			primaryStage.setX(300);
			primaryStage.setY(300);
			primaryStage.setWidth(500);
			primaryStage.setHeight(500);
			
			primaryStage.show();
		}
		
		private void fillTableView(TableView tableView) {
	        tableView.getItems().clear();
	        List<Transactionn> transactions = getTransactions();
	        for (Transactionn t : transactions) {
	        	tableView.getItems().add(t);
			}
		}
		
		private List<Transactionn> getTransactions() {

			Gson gson = new Gson();

			String responseTransactionsList = service.path("getTransactions")
					.request(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.get(String.class);

			List<Transactionn> transactions = Arrays.asList(gson.fromJson(responseTransactionsList, Transactionn[].class));
					
			return transactions;

		}
		
		
		private void showAddUpdateTransactionStage(Stage primaryStage, Transactionn transaction) {
    	   	
	    	Stage stage = new Stage();
	        
	    	stage.setTitle("Add or Update Transaction - in Modal Mode");
	        stage.setX(300);
	        stage.setY(300);
	        stage.setWidth(300);
	        stage.setHeight(300);
	        
			//FlowPane root = new FlowPane();
			GridPane root = new GridPane();

			Label lblDescription = new Label("Description");
			root.add(lblDescription, 0, 0, 1, 1);
			//root.getChildren().add(lblTitle);

			TextField txtDescription = new TextField();
			lblDescription.setId("lblDescricao");
			root.add(txtDescription, 1, 0, 1, 1);
			//root.getChildren().add(txtTitle);

			Label lblType = new Label("Type");
			//root.getChildren().add(lblAuthor);
			root.add(lblType, 0, 1, 1, 1);

			TextField txtType = new TextField();
			txtType.setId("lblTipo");
			root.add(txtType, 1, 1, 1, 1);
			//root.getChildren().add(txtAuthor);

			Label lblDate = new Label("Date");
			root.add(lblDate, 0, 2, 1, 1);
			//root.getChildren().add(lblIsAvailable);
			
			TextField txtDate = new TextField();
			lblDate.setId("lblDate");
			root.add(txtDate, 1, 2, 1, 1);
			//root.getChildren().add(txtAuthor);
			
			Label lblAmount = new Label("Amount");
			root.add(lblAmount, 0, 3, 1, 1);
			//root.getChildren().add(lblIsAvailable);
			
			TextField txtAmount = new TextField();
			lblAmount.setId("lblAmount");
			root.add(txtAmount, 1, 3, 1, 1);
			//root.getChildren().add(txtAuthor);

			
			Button btnSave = new Button("Save");
			Button btnCancel = new Button("Cancel");
			root.add(btnSave, 0, 4, 1, 1);
			root.add(btnCancel, 1, 4, 1, 1);
			
			root.setHgap(5);
			root.setVgap(5);
			
			if (transaction.getId() != 0) {
				txtDescription.setText(transaction.getNotes());
				txtType.setText(transaction.getType());
				txtDate.setText(transaction.getDate());
				txtAmount.setText(Double.toString(transaction.getAmount()));
				
				
			}

			btnSave.setOnAction(ae -> {
				
		
				transaction.setNotes(txtDescription.getText());
				transaction.setType(txtType.getText());
				transaction.setDate(txtDate.getText());
				transaction.setAmount(Double.parseDouble(txtAmount.getText()));
				System.out.println("Saving data... " + transaction);
				saveData(transaction);
				stage.close();
			});

			btnCancel.setOnAction(ae -> {
				System.out.println("Cancelling...");
				cleanFields(root);
				stage.close();
			});

			//root.getChildren().addAll(new Button[] { btnSave, btnCancel });
			
			root.setAlignment(Pos.CENTER);

			Scene scene = new Scene(root);
			stage.setTitle("Simple Input Screen Example");
			stage.setScene(scene);
			      
	        
	        stage.initOwner(primaryStage);
	        //stage.initModality(Modality.NONE);
	        stage.initModality(Modality.WINDOW_MODAL);
	        //stage.initModality(Modality.APPLICATION_MODAL);


	        stage.showAndWait();
	    }
		
		private void saveData(Transactionn transaction) {		
			try {
				
				Response response;
				String message = "Transaction added successfully.";

				if (transaction.getId() != 0) {
					
					message = "Transaction updated successfully.";				
					response = service.path("updateTransaction").request(MediaType.APPLICATION_JSON)
							.put(Entity.entity(transaction, MediaType.APPLICATION_JSON), Response.class);
				} else {
					
					message = "Transaction added successfully.";
					response = service.path("addTransaction").request(MediaType.APPLICATION_JSON)
							.post(Entity.entity(transaction, MediaType.APPLICATION_JSON), Response.class);
				}		
				
				if (response.getStatus() < 200 || response.getStatus() > 299) {			
					showMessage("Failed : HTTP error code : " + response.getStatus(), AlertType.ERROR);
					//throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
				}else {
					showMessage(message, AlertType.INFORMATION);
				}
			} catch (Exception e) {
				showMessage("Error while saving the transaction.", AlertType.ERROR);
				//throw new RuntimeException("Failed to save the book.");
			}
		}
		
		private void cleanFields(GridPane root) {
			
			for (Node node : root.getChildren()) {
			    System.out.println("Id: " + node.getId());
			    if (node instanceof TextField) {
			        // clear
			        ((TextField)node).setText("");
			    } else if (node instanceof CheckBox) {
			        // clear
			        ((CheckBox)node).setSelected(false);
			    }
			}
			
		}
		
		private void showMessage(String message, AlertType alertType) {
			Alert alert = new Alert(alertType);
	        alert.setTitle("Transactions Application");
	        alert.setHeaderText(message);
	        //alert.setContentText(message);
	        alert.showAndWait();
		}
		
		private boolean showConfirmationDialog(String confirmationMessage) {
			ButtonType okBtn = new ButtonType("Yes", ButtonData.OK_DONE);
			ButtonType closeBtn = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
			Alert alert = new Alert(AlertType.WARNING,
			        confirmationMessage,
			        okBtn,
			        closeBtn);

			alert.setTitle("Delete transaction warning");
			Optional<ButtonType> result = alert.showAndWait();

			return (result.orElse(closeBtn) == okBtn);
		}
} 


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
import financialApp.Category;

public class CategoriesClient extends Application {
	
		private static URI getBaseURI() {
			return UriBuilder.fromUri("http://localhost:8080/RestServer/category/").build();
		}
	
		private static ClientConfig config = new ClientConfig();
		private static Client client = ClientBuilder.newClient(config);
		private static WebTarget service = client.target(getBaseURI());
	
 		public static void main(String[] args) {  
			launch(args); 
		}
		 
		@Override
		public void start(Stage primaryStage) throws Exception {

			
			GridPane root = new GridPane();
			
			TableView tableView = new TableView();
	
	        TableColumn<Category, String> column1 = new TableColumn<>("Name");
	        column1.setMinWidth(200);
	        column1.setCellValueFactory(new PropertyValueFactory<>("name"));

	        TableColumn<Category, String> column2 = new TableColumn<>("Limit");
	        column2.setMinWidth(200);
	        column2.setCellValueFactory(new PropertyValueFactory<>("limit"));
	  
	        tableView.getColumns().add(column1);
	        tableView.getColumns().add(column2);
	        
	        fillTableView(tableView);

	        /*This command line gets the selected row and the corresponding Model Instance (Book)*/
	        TableView.TableViewSelectionModel<Category> selectionModel = tableView.getSelectionModel();

	        /* You can choose between single and multiple selection*/
	        selectionModel.setSelectionMode(SelectionMode.SINGLE);
	        //selectionModel.setSelectionMode(SelectionMode.MULTIPLE);

	        /* Here is a list for the selected items in the table view */
	        ObservableList<Category> selectedItems = selectionModel.getSelectedItems();

	        /* In case you need to check the selected item when it is changed */
	        selectedItems.addListener(new ListChangeListener<Category>() {
	            @Override
	            public void onChanged(Change<? extends Category> change) {
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
	        
	        
	        root.add(tableView, 0, 0, 4, 1);
			
	        Button btnNew = new Button("New");
			Button btnEdit = new Button("Edit");	
			Button btnDelete = new Button("Delete");
			Button btnCancel = new Button("Cancel");
			
			btnNew.setOnAction(ae -> { 
				System.out.println("New category data... ");
				showAddUpdateCategoriesStage(primaryStage, new Category()); 
				fillTableView(tableView);
	        });
							
			btnEdit.setOnAction(ae -> {
				System.out.println("Editing data... ");			
				showAddUpdateCategoriesStage(primaryStage, selectedItems.get(0));
				fillTableView(tableView);
			});
			
			btnDelete.setOnAction(ae -> {
				System.out.println("Deleting data... ");				

				if (showConfirmationDialog("Are you sure you want to delete the category? ")) {	
					deleteCategory(selectedItems.get(0).getId());
					fillTableView(tableView);
				}			
				
			});
			
			btnCancel.setOnAction(ae -> {
				System.out.println("Cancelling...");
				fillTableView(tableView);
				primaryStage.close();
			});
			
			
			root.add(btnNew, 0, 1, 1, 1);
			root.add(btnEdit, 1, 1, 1, 1);
			root.add(btnDelete, 2, 1, 1, 1);
			root.add(btnCancel, 3, 1, 1, 1);
			
			root.setHgap(5);
			root.setVgap(5);
							
			Scene scene = new Scene(root);
			primaryStage.setTitle("Simple Categories CRUD example");
			primaryStage.setScene(scene);
			primaryStage.setX(300);
			primaryStage.setY(300);
			primaryStage.setWidth(500);
			primaryStage.setHeight(500);
			
			primaryStage.show();
		}
		
		private void fillTableView(TableView tableView) {
	        tableView.getItems().clear();
	        List<Category> categories = getCategories();
	        for (Category c : categories) {
	        	tableView.getItems().add(c);
			}
		}
		
		private List<Category> getCategories() {

			Gson gson = new Gson();

			String responseCategoriesList = service.path("getCategories")
					.request(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.get(String.class);

			List<Category> categories = Arrays.asList(gson.fromJson(responseCategoriesList, Category[].class));
					
			return categories;

		}
		
		private boolean deleteCategory(int categoryId) {
			
			Response response = service.path("deleteCategory")
					.path(Integer.toString(categoryId)).request().delete();
	
			if (response.getStatus() < 200 || response.getStatus() > 299) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			} 
			
			return response.getStatus() == 200;

		}
		
		
		private void showAddUpdateCategoriesStage(Stage primaryStage, Category category) {
    	   	
	    	Stage stage = new Stage();
	        
	    	stage.setTitle("Add or Update  Category - in Modal Mode");
	        stage.setX(300);
	        stage.setY(300);
	        stage.setWidth(300);
	        stage.setHeight(300);
	       
			GridPane root = new GridPane();

			Label lblName = new Label("Name");
			root.add(lblName, 0, 0, 1, 1);
			
			TextField txtName = new TextField();
			lblName.setId("lblName");
			root.add(txtName, 1, 0, 1, 1);
			
			Label lblLimit = new Label("Limit");
			root.add(lblLimit, 0, 1, 1, 1);

			TextField txtLimit = new TextField();
			lblLimit.setId("lblLimit");
			root.add(txtLimit, 1, 1, 1, 1);
		
			Button btnSave = new Button("Save");
			Button btnCancel = new Button("Cancel");
			root.add(btnSave, 0, 3, 1, 1);
			root.add(btnCancel, 1, 3, 1, 1);
			
			root.setHgap(5);
			root.setVgap(5);
			
			if (category.getId() != 0) {
				txtName.setText(category.getName());
				txtLimit.setText(Double.toString(category.getLimit()));
			}

			btnSave.setOnAction(ae -> {
				System.out.println("Saving data... " + category);
				
				category.setName(txtName.getText());
				category.setLimit(Double.parseDouble(txtLimit.getText()));
				saveData(category);
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
		
		private void saveData(Category category) {		
			try {
				
				Response response;
				String message = "Category added successfully.";
				
				if (category.getId() != 0) {
					
					message = "Category updated successfully.";				
					response = service.path("updateCategory").request(MediaType.APPLICATION_JSON)
							.put(Entity.entity(category, MediaType.APPLICATION_JSON), Response.class);
				} else {
					
					message = "Category added successfully.";
					response = service.path("addCategory").request(MediaType.APPLICATION_JSON)
							.post(Entity.entity(category, MediaType.APPLICATION_JSON), Response.class);
				}		
				
				if (response.getStatus() < 200 || response.getStatus() > 299) {			
					showMessage("Failed : HTTP error code : " + response.getStatus(), AlertType.ERROR);
					//throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
				}else {
					showMessage(message, AlertType.INFORMATION);
				}
			} catch (Exception e) {
				showMessage("Error while saving the Category.", AlertType.ERROR);
				
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
	        alert.setTitle("Categories Application");
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

			alert.setTitle("Delete category warning");
			Optional<ButtonType> result = alert.showAndWait();

			return (result.orElse(closeBtn) == okBtn);
		}
} 


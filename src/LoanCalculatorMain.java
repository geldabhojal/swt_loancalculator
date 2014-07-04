/*
 * Author : Bhojal Gelda
 * Purpose: Main Thread
 */
import java.awt.EventQueue;

import my.bo.LoanCalculatorModel;
import my.controllers.MyFrontController;
import my.views.LoanCalculatorView;


public class LoanCalculatorMain {
	
	public static void main(String[] args) {
		  /* Use an appropriate Look and Feel */
   
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//Instantiating View
					LoanCalculatorView view = new LoanCalculatorView();
					
					//Instantiating Model
					LoanCalculatorModel model = new LoanCalculatorModel();
					
					//Registering View and Model with the Controller.
					MyFrontController controller = new MyFrontController(view, model);
					
					//Make the View Frame visible
					view.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
}

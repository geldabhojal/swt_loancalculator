/*
 * Author : Bhojal Gelda
 * Purpose: This is the Business Object.
 * 
 */

package my.bo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import my.beans.MonthlyRowData;

public class LoanCalculatorModel {
	private static List<MonthlyRowData> rowList;
	 //Compute the monthly payment based on the loan amount,Rate, and length of loan.
    public static double computePayment(double loanAmt, double rate, int numPeriods) {
        double I, partial1, denominator, answer;
         
        if (rate > 0.01) {
            I = rate / 100.0 / 12.0;         //get monthly rate from annual
            partial1 = Math.pow((1 + I), (0.0 - numPeriods));
            denominator = (1 - partial1) / I;
        } else { //rate ~= 0
            denominator = numPeriods;
        }
         
        answer = (loanAmt) / denominator;
        return answer;
    }
    
    /*
     * Using the formula FV=PV*(1+r)^n - P*[((1+r)^n-1)/r]
     * where FV = Future Value / Remaining balance
     * PV= Present Value
     * P= Payment
     * r= rate per payment
     * n = number of payments
     * 
     * loading the MonthlyRowData object with the result which will become
     * the row of the chart to be displayed.
     * We are also assuming that the first payment is due one month 
     * from the start of the loan.
     */
    
    public static List<MonthlyRowData> computeChartData(double principalAmt, double rate, int term,double monthlyPayment)
    {
    	try{
	    	rowList = new ArrayList<MonthlyRowData>();
		    BigDecimal monthlyRate = new BigDecimal((rate/100)/12);
		    monthlyRate= monthlyRate.setScale(4,BigDecimal.ROUND_HALF_UP);
		    
		    for(int month=1;month<=term;month++){
				BigDecimal result=new BigDecimal(Math.pow((1+monthlyRate.doubleValue()), month));
				result=result.setScale(4, BigDecimal.ROUND_HALF_UP);
				BigDecimal remainingBal_1 =new BigDecimal(principalAmt*result.doubleValue());
				BigDecimal remainingBal_2=new BigDecimal(monthlyPayment*((result.doubleValue()-1)/monthlyRate.doubleValue()));
				
				remainingBal_1=remainingBal_1.setScale(2,BigDecimal.ROUND_HALF_UP);
				remainingBal_2=remainingBal_2.setScale(2,BigDecimal.ROUND_HALF_UP);
				
				BigDecimal remainingBalance=new BigDecimal(remainingBal_1.doubleValue()-remainingBal_2.doubleValue());
				remainingBalance=remainingBalance.setScale(2, BigDecimal.ROUND_HALF_UP);
				if(remainingBalance.doubleValue()<0){
					remainingBalance = new BigDecimal(0);
				}
				BigDecimal intAmt=new BigDecimal(remainingBalance.doubleValue()*monthlyRate.doubleValue());
				intAmt=intAmt.setScale(2, BigDecimal.ROUND_HALF_UP);
				BigDecimal payAmt = new BigDecimal(monthlyPayment-intAmt.doubleValue());
				payAmt=payAmt.setScale(2, BigDecimal.ROUND_HALF_UP);
				rowList.add(new MonthlyRowData(month,payAmt.doubleValue(),intAmt.doubleValue(),remainingBalance.doubleValue()));
			}
    	}catch(ArithmeticException ae){return null;}
	    return rowList;
    }
}

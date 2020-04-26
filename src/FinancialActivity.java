/**
 * This interface will enable unique implementations for update cash and buySell when we
 * add support for different asset classes in the future, which will require unique
 * implementations.
 *
 */
public interface FinancialActivity {
	public void updateCash(double marketValue);
	public void buySell(String name, double units);
	
}

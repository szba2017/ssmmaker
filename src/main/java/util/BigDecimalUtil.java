package util;

import java.math.BigDecimal;

public class BigDecimalUtil {

	private BigDecimalUtil() {}
	
	public static BigDecimal add(double v1,double v2) {
		BigDecimal decimal = new BigDecimal(Double.toString(v1));
		BigDecimal decima2 = new BigDecimal(Double.toString(v2));
		return decimal.add(decima2);
	}
	
	public static BigDecimal sub(double v1,double v2) {
		BigDecimal decimal = new BigDecimal(Double.toString(v1));
		BigDecimal decima2 = new BigDecimal(Double.toString(v2));
		return decimal.subtract(decima2);
	}	
	
	public static BigDecimal mul(double v1,double v2) {
		BigDecimal decimal = new BigDecimal(Double.toString(v1));
		BigDecimal decima2 = new BigDecimal(Double.toString(v2));
		return decimal.multiply(decima2);
	}
	
	public static BigDecimal div(double v1,double v2) {
		BigDecimal decimal = new BigDecimal(Double.toString(v1));
		BigDecimal decima2 = new BigDecimal(Double.toString(v2));
		return decimal.divide(decima2,2,BigDecimal.ROUND_HALF_UP);//四舍五入，保留两位小数
		
	}
	
}

package mil.usmc.mgrs;

/**
 * A WMS request will have a query parameter like:
 * 
 * BBOX={left}, {bottom}, {right}, {top}
 * @author hastings
 */
public class WmsBoundingBox {
	private double left;
	private double bottom;
	private double right;
	private double top;
	
	public WmsBoundingBox() {
	}

	public WmsBoundingBox(double left, double bottom, double right, double top) {
		this.left = left;
		this.bottom = bottom;
		this.right = right;
		this.top = top;
	}
	
	public WmsBoundingBox left(double v) {
		left = v;
		return this;
	}
	public double left() {
		return left;
	}
	
	public WmsBoundingBox bottom(double v) {
		bottom = v;
		return this;
	}
	public double bottom() {
		return bottom;
	}
	
	public WmsBoundingBox right(double v) {
		right = v;
		return this;
	}
	public double right() {
		return right;
		
	}
	public WmsBoundingBox top(double v) {
		top = v;
		return this;
	}
	public double top() {
		return top;
	}
	
	public double getLngDegSpan(){
		double dist = right - left;
		if ( dist < 0.0 ){
			// contains the 180 mark.
			dist += 360;
		}
		return dist;
	}
	
	/**
	 * Parses a string of the format [{left}, {bottom}, {right}, {top}] 
	 * @param string
	 * @return this
	 */
	public WmsBoundingBox parseValue(String string) {
		String trim = string.trim();
		
		String[] parts = trim.split(",[ \t]*");
		if (parts.length != 4) {
			throw new IllegalArgumentException("Valued to parse. Expected [{left}, {bottom}, {right}, {top}], value was " + string);
		}
		
		left = parseDouble(parts[0], "left", string);
		bottom = parseDouble(parts[1], "bottom", string);
		right = parseDouble(parts[2], "right", string);
		top = parseDouble(parts[3], "top", string);
		
		return this;
	}
	
	public String format() {
		return String.format("[%f,%f,%f,%f]", left, bottom, right, top);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(bottom);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(left);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(right);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(top);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WmsBoundingBox other = (WmsBoundingBox) obj;
		if (Double.doubleToLongBits(bottom) != Double
				.doubleToLongBits(other.bottom))
			return false;
		if (Double.doubleToLongBits(left) != Double
				.doubleToLongBits(other.left))
			return false;
		if (Double.doubleToLongBits(right) != Double
				.doubleToLongBits(other.right))
			return false;
		if (Double.doubleToLongBits(top) != Double.doubleToLongBits(other.top))
			return false;
		return true;
	}

	private double parseDouble(String v, String name, String string) {
		try {
			return Double.parseDouble(v);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Unabled to parse {"+ name +"} as double, value was " + string);
		}
	}
}

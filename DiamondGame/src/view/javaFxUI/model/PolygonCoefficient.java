package view.javaFxUI.model;

/**
 * 多角形の係数情報
 * @author yone
 *
 */
public class PolygonCoefficient {
	int apexNum;
	double k[];
	double x[];
	double y[];
	public PolygonCoefficient(int apexNum) {
		this.apexNum = apexNum;
		k = new double[this.apexNum];
		x = new double[this.apexNum];
		y = new double[this.apexNum];
	}

	/**
	 * 指定された多角形の係数情報({@link PolygonCoefficient}を返却する<br>
	 * 6角系の場合の例
	 * i(6) k=  0.00, x= 1.000000, y= 0.000000<br>
	 * i(6) k= 60.00, x= 0.500000, y= 0.866025<br>
	 * i(6) k=120.00, x=-0.500000, y= 0.866025<br>
	 * i(6) k=180.00, x=-1.000000, y= 0.000000<br>
	 * i(6) k=240.00, x=-0.500000, y=-0.866025<br>
	 * i(6) k=300.00, x= 0.500000, y=-0.866025<br>
	 * i(6) k=360.00, x= 1.000000, y=-0.000000<br>
	 * @param apexNum
	 * @return
	 */
	public static PolygonCoefficient getPolygonCoefficient(int apexNum){
		assert apexNum >= 3;

		double x,y,k;
		int i;
		double offset = (90*Math.PI/180);
		PolygonCoefficient ret = new PolygonCoefficient(apexNum);

		for(k=offset, i=0; i<apexNum; k+=2*Math.PI/apexNum, i++){
			x = Math.cos(k);
			y = Math.sin(k);

			ret.x[i] = x;
			ret.y[i] = y;
			ret.k[i] = k;

			// System.out.printf("apex(%d) k=%6.2f, x=%4.2f, y=%4.2f\n", apexNum, k/Math.PI*180,x,y);
		}

		assert ret.x.length==apexNum && ret.y.length==apexNum  && ret.k.length==apexNum ;
		return ret;
	}
}
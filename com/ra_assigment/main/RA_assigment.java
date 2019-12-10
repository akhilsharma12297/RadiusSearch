package com.ra_assigment.main;

import java.util.ArrayList;
import java.sql.*;

public class RA_assigment {

	public static void main(String[] args) {

		int radius = 10; // Property under the distance "radius" will be considered

		int latitude = 7; // My current latitude

		int longitude = 7; // My current longitude

		int min_budget = -1; // If value is not entered by the user, -1 is sent to the program. Same is for
								// no. of beds , no. of Bathrooms

		int max_budget = 5000;

		int min_bedroom = -1;

		int max_bedroom = 2;

		int min_bathroom = -1;

		int max_bathroom = 2;

		driverFunction(radius, latitude, longitude, min_budget, max_budget, min_bedroom, max_bedroom, min_bathroom,
				max_bathroom);

	}

	public static void driverFunction(int radius, int latitude, int longitude, int min_budget, int max_budget,
			int min_bedroom, int max_bedroom, int min_bathroom, int max_bathroom) {

		if (!(max_bathroom >= min_bathroom) || !(max_bedroom >= min_bedroom) || !(max_budget >= min_budget)
				|| !(radius > 1)) {
			System.out.println("Wrong input , please check the input.");
		}

		Budget budget = new Budget(); // Budget has 3 members:-
										// "min" minimum budget range by user , -1 for no input
										// "max" maximum budget range by user , -1 for no input
										// "bit" bit is "true" when both both value are provided

		budget.min = min_budget;
		budget.max = max_budget;

		if (min_budget != -1 && max_budget != -1) {
			budget.bit = true;
		}

		Bedroom bedroom = new Bedroom(); // Budget has 3 members:-
											// "min" minimum no. of beds range by user , -1 for no input
											// "max" maximum no. of beds range by user , -1 for no input
											// "bit" bit is "true" when both both value are provided

		bedroom.min = min_bedroom;
		bedroom.max = max_bedroom;

		if (min_bedroom != -1 && max_bedroom != -1) {
			bedroom.bit = true;
		}

		Bathroom bathroom = new Bathroom(); // Budget has 3 members:-
											// "min" minimum no. of bathroom range by user , -1 for no input
											// "max" maximum no. of bathroom range by user , -1 for no input
											// "bit" bit is "true" when both both value are provided

		bathroom.min = min_bathroom;
		bathroom.max = max_bathroom;

		if (min_bathroom != -1 && max_bathroom != -1) {
			bedroom.bit = true;
		}

		ArrayList<Property> list = obtainDataFromDb_and_Validate(radius, latitude, longitude, budget, bedroom,
				bathroom);

		scoreMatch(list, latitude, longitude, budget, bedroom, bathroom);
	}

	static class Property {
		int id;
		int latitude;
		int longitude;
		int price;
		int no_bed;
		int no_bath;

		int match;

		Property(int id, int latitude, int longitude, int price, int no_bed, int no_bath, int match) {
			this.id = id;
			this.latitude = latitude;
			this.longitude = longitude;
			this.price = price;
			this.no_bed = no_bed;
			this.no_bath = no_bath;
			this.match = match;
		}
	}

	static class Budget {
		int min = -1;
		int max = -1;
		boolean bit;

		public void SetBit() {
			if (min > -1 && max > -1) {
				this.bit = true;
			} else
				this.bit = false;
		}
	}

	static class Bedroom {
		int min = -1;
		int max = -1;
		boolean bit;

		public void SetBit() {
			if (min > -1 && max > -1) {
				this.bit = true;
			} else
				this.bit = false;
		}
	}

	static class Bathroom {
		int min = -1;
		int max = -1;
		boolean bit;

		public void SetBit() {
			if (min > -1 && max > -1) {
				this.bit = true;
			} else
				this.bit = false;
		}

	}

	/*
	 * "obtainDataFromDb_and_Validate" function takes the input value form the user
	 * ie. radius , latitude , longitude ,no of bed range , no of bathroom
	 * 
	 * The function makes connection to the database name - "radiusagent" and
	 * obtaions data from * "propertytable" having 6 columns "id" , "latitude" ,
	 * "longitude" ,"price" ,"no_bath", "no_bath".
	 * 
	 * while accessing these values row wise it is send to "validate".
	 * 
	 */

	private static ArrayList<Property> obtainDataFromDb_and_Validate(int radius, int latitude, int longitude,
			Budget budget, Bedroom bedroom, Bathroom bathroom) {

		ArrayList<Property> list = new ArrayList<Property>();

		Connection conn = null;
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "radiusagent";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "root";
		String password = "";
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url + dbName, userName, password);
			String query = "Select * FROM propertytable";
			System.out.println("Connected to the database. Obtaining data.");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {

				validate(rs, list, radius, latitude, longitude, budget, bedroom, bathroom);

			}
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;

	}

	/*
	 * "validate" function takes each ResultSet and find the distance b/w the two
	 * using "find_distance" Only property which who's distance is less than
	 * "radius" are only considered .
	 * 
	 * For budget ,bed ,bathroom which may have 2 values "min" and "max" , if not
	 * entered by the user they are -1 .
	 * 
	 * If both value are provided then then -20% / -2 of min and +20% / +2 of max
	 * are set for the search criteria. If only min or max is given then +/-20%
	 * (+/-2 for bed and bathroom) of that value is given.
	 * 
	 * The value which fulfill are the added to the ArrayList . The distance value
	 * for the property which is less than 2 is given 40 points in the match
	 * attribute.
	 * 
	 * The shortlisted properties are then added to the ArrayList for the futher
	 * match with respect to the input.
	 * 
	 */

	public static void validate(ResultSet rs, ArrayList<Property> list, int radius, int latitude, int longitude,
			Budget budget, Bedroom bed, Bathroom bathroom) throws SQLException {

		double distance = find_distance(latitude, longitude, rs.getInt(2), rs.getInt(3));

		int match = 0;

		double budget_min = 0;
		double budget_max = 0;

		if (budget.bit == true) {
			budget_min = budget.min * 0.75;
			budget_max = budget.max * 1.25;
		} else if (budget.min != -1) {

			budget_min = budget.min * 0.75;
			budget_max = budget.min * 1.25;

		} else if (budget.max != -1) {
			budget_min = budget.max * 0.75;
			budget_max = budget.max * 1.25;

		}

		double bed_min = 0;
		double bed_max = 0;

		if (budget.bit == true) {
			bed_min = bed.min - 2;
			bed_max = bed.max + 2;
		} else if (bed.min != -1) {
			bed_min = bed.min - 2;
			bed_max = bed.min + 2;
		} else if (budget.max != -1) {
			bed_min = bed.max - 2;
			bed_max = bed.max + 2;
		}

		double bathroom_min = 0;
		double bathroom_max = 0;

		if (budget.bit == true) {
			bathroom_min = bathroom.min - 2;
			bathroom_max = bathroom.max + 2;
		} else if (budget.min != -1) {
			bathroom_min = bathroom.min - 2;
			bathroom_max = bathroom.min + 2;
		} else if (budget.max != -1) {
			bathroom_min = bathroom.max - 2;
			bathroom_max = bathroom.max + 2;
		}

		if ((distance <= 10) && (budget.min <= rs.getInt(4) && rs.getInt(4) <= budget.max)
				&& (bed_min <= rs.getInt(5) && rs.getInt(5) <= bed_max)
				&& (bathroom_min <= rs.getInt(6) && rs.getInt(6) <= bathroom_max)) {

			if (distance <= 2) {
				match = 30;
			}

			list.add(new Property(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6),
					match));

		}

	}

	/*
	 * "rateMatch" goes through the ArrayList which has matching the properties
	 * According to the requirement by the user. The properties are rated.
	 * "budget_parameter" , "bedroom_parameter" , "bathroom_parameter" are the
	 * Functions which are used to match the properties on budget , bed , bathroom
	 * respectively. Matching on the 10 % margin range on both side and correct
	 * match gets score acc. to the problem statement and the value is decrease as
	 * it deviates , score is alloted accordingly.
	 * 
	 * The property which are 40 and above in the score card are considered.
	 * 
	 */

	private static void scoreMatch(ArrayList<Property> list, int latitude, int longitude, Budget budget, Bedroom bed,
			Bathroom bathroom) {

		boolean matchFound = false;

		for (int i = 0; i < list.size(); i++) {

			System.out.println();

			Property temp = list.get(i);

			budget_parameter(temp, budget);

			bedroom_parameter(temp, bed);

			bathroom_parameter(temp, bathroom);

			if (temp.match >= 40) {

				matchFound = true;

				System.out.print("S.No " + i + " -> ID :- " + temp.id + " ");
				System.out.print("Latitude :- " + temp.latitude + " ");
				System.out.print("longitude :- " + temp.longitude + " ");
				System.out.print("Price :- " + temp.price + " ");
				System.out.print("No of Bathrooms :- " + temp.no_bath + " ");
				System.out.print("No of Bathrooms :- " + temp.no_bed + " ");
				System.out.print("No of Bathrooms :- " + temp.price + " ");
				System.out.print("Match percentage :- " + temp.match + "%");

				System.out.println();
			}
		}

		if (matchFound == false || list.size() == 0) {
			System.out.println("Match not found.");
		}

	}

	private static double find_distance(int x1, int y1, int x2, int y2) {
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));

	}

	public static void budget_parameter(Property temp, Budget budget) {
		double floor = 0;
		double ceil = 0;

		if (budget.bit == true) {

			floor = budget.min;
			ceil = budget.max;

		} else {

			if (budget.min != -1) {

				floor = budget.min * 0.90;
				ceil = budget.min * 1.10;

			} else if (budget.max != -1) {

				floor = budget.max * 0.90;
				ceil = budget.max * 1.10;

			}
		}

		if (floor <= temp.price && ceil >= temp.price) {
			temp.match += 30;
		}
	}

	public static void bedroom_parameter(Property temp, Bedroom bed) {

		if (bed.bit == true) {

			if (bed.min <= temp.price && bed.max >= temp.price) {
				temp.match += 20;
			}
		} else {

			if (bed.min != -1) {

				int offset = temp.no_bed - bed.min;
				if (offset == 0) {
					temp.match += 20;
				} else if (Math.abs(offset) == 1) {
					temp.match += 10;
				} else if (Math.abs(offset) == 2) {
					temp.match += 5;
				}

			} else if (bed.max != -1) {

				int offset = temp.no_bed - bed.max;
				if (offset == 0) {
					temp.match += 20;
				} else if (Math.abs(offset) == 1) {
					temp.match += 10;
				} else if (Math.abs(offset) == 2) {
					temp.match += 5;
				}

			}
		}

	}

	public static void bathroom_parameter(Property temp, Bathroom bathroom) {

		if (bathroom.bit == true) {

			if (bathroom.min <= temp.price && bathroom.max >= temp.price) {
				temp.match += 20;
			}

		} else {

			if (bathroom.min != -1) {

				int offset = temp.no_bed - bathroom.min;
				if (offset == 0) {
					temp.match += 20;
				} else if (Math.abs(offset) == 1) {
					temp.match += 10;
				} else if (Math.abs(offset) == 2) {
					temp.match += 5;
				}
			} else if (bathroom.max != -1) {

				int offset = temp.no_bed - bathroom.max;
				if (offset == 0) {
					temp.match += 20;
				} else if (Math.abs(offset) == 1) {
					temp.match += 10;
				} else if (Math.abs(offset) == 2) {
					temp.match += 5;
				}
			}
		}

	}

}
package com.ra_assigment.main;

import java.util.ArrayList;
import java.sql.*;

public class RA_assigment {

	public static void main(String[] args) {

		int radius = 10; // Property under the distance "radius" will be considered

		int latitude = 7; // My current latitude

		int longtiude = 7; // My current longitude  

		Budget budget = new Budget(); // Budget has 3 members:-
									  // "min" minimum budget range by user , -1 for no input
									  // "max" maximum budget range by user , -1 for no input
									  // "bit" bit is "true" when both both value are provided

		budget.max = 5000; // Value for max Budgets

		Bed bed = new Bed(); // Budget has 3 members:-
							 // "min" minimum no. of beds range by user , -1 for no input
							 // "max" maximum no. of beds range by user , -1 for no input
		                     // "bit" bit is "true" when both both value are provided

		bed.max = 2; // Value for max no. of beds

		Bathroom bathroom = new Bathroom(); // Budget has 3 members:-
											// "min" minimum no. of bathroom range by user , -1 for no input
		 									// "max" maximum no. of bathroom range by user , -1 for no input
											// "bit" bit is "true" when both both value are provided

		bathroom.max = 2; // Value for max no. of bathroom

		ArrayList<Property> list = obtainDataFromDb_and_Validate(radius, latitude, longtiude, budget, bed, bathroom);

		match(list, latitude, longtiude, budget, bed, bathroom);

	}

	static class Property {
		int id;
		int latitude;
		int longtiude;
		int price;
		int no_bed;
		int no_bath;

		int match;

		Property(int id, int latitude, int longtiude, int price, int no_bed, int no_bath, int match) {
			this.id = id;
			this.latitude = latitude;
			this.longtiude = longtiude;
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

	static class Bed {
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

	private static ArrayList<Property> obtainDataFromDb_and_Validate(int radius, int latitude, int longtiude,
			Budget budget, Bed bed, Bathroom bathroom) {

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

				validate(rs, list, radius, latitude, longtiude, budget, bed, bathroom);

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

	public static void validate(ResultSet rs, ArrayList<Property> list, int radius, int latitude, int longtiude,
			Budget budget, Bed bed, Bathroom bathroom) throws SQLException {

		double distance = Find_distance(latitude, longtiude, rs.getInt(2), rs.getInt(3));

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

	public static void bedroom_parameter(Property temp, Bed bed) {

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

	private static double Find_distance(int x1, int y1, int x2, int y2) {
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));

	}

	private static void match(ArrayList<Property> list, int latitude, int longtiude, Budget budget, Bed bed,
			Bathroom bathroom) {

		for (int i = 0; i < list.size(); i++) {

			System.out.println();

			Property temp = list.get(i);

			budget_parameter(temp, budget);

			bedroom_parameter(temp, bed);

			bathroom_parameter(temp, bathroom);

			if (temp.match >= 40) {

				System.out.print("S.No " + i + " -> ID :- " + temp.id + " ");
				System.out.print("Latitude :- " + temp.latitude + " ");
				System.out.print("longtiude :- " + temp.longtiude + " ");
				System.out.print("Price :- " + temp.price + " ");
				System.out.print("No of Bathrooms :- " + temp.no_bath + " ");
				System.out.print("No of Bathrooms :- " + temp.no_bed + " ");
				System.out.print("No of Bathrooms :- " + temp.price + " ");
				System.out.print("Match percentage :- " + temp.match + "%");

				System.out.println();
			}
		}

	}

}

package com.ra_assigment.function_file;

import java.util.ArrayList;

public class RA_assigment {

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

	public static void main(String[] args) {

		int latitude = 7;

		int longtiude = 7;

		Budget budget = new Budget();

		budget.max = 5000;

		Bed bed = new Bed();

		bed.max = 2;

		Bathroom bathroom = new Bathroom();

		bathroom.max = 2;

		Property temp = new Property(1, 5, 7, 5000, 2, 2);

		ArrayList<Property> list = new ArrayList<>();

		list.add(temp);

		driverFunc(list, latitude, longtiude, budget, bed, bathroom);
	}

	static class Property {
		int id;
		int latitude;
		int longtiude;
		int price;
		int no_bed;
		int no_bath;

		int match;

		Property(int id, int latitude, int longtiude, int price, int no_bed, int no_bath) {
			this.id = id;
			this.latitude = latitude;
			this.longtiude = longtiude;
			this.price = price;
			this.no_bed = no_bed;
			this.no_bath = no_bath;
		}
	}

	private static void driverFunc(ArrayList<Property> list, int latitude, int longtiude, Budget budget, Bed bed,
			Bathroom bathroom) {

		for (int i = 0; i < list.size(); i++) {

			Property temp = list.get(i);

			double distance = Find_distance(latitude, longtiude, temp.latitude, temp.longtiude);

			if (distance <= 2) {
				temp.match += 30;
			}

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

			if (!(distance <= 10) || !((budget.min * 0.75) < temp.price && temp.price < (budget.max * 1.25))
					|| !((bed_min * 0.75) < temp.no_bed && temp.no_bed < (bed_max * 1.25))
					|| !((bathroom_min * 0.75) < temp.no_bath && temp.no_bath < (bathroom_max * 1.25))) {

				swap_and_remove(list, i);

			}

			budget_parameter(temp, budget);

			bedroom_parameter(temp, bed);

			bathroom_parameter(temp, bathroom);

		}

		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).match);
		}

	}

	public static void swap_and_remove(ArrayList<Property> list, int i) {

		Property temp = list.get(i);

		Property last = list.get(list.size() - 1);

		list.set(list.size() - 1, temp);

		list.set(i, last);

		list.remove(list.size() - 1);
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

}

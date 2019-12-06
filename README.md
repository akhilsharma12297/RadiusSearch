# RadiusAgent Assigment

![RadiusAgent](https://miro.medium.com/max/623/1*lQtTOp_q3oI12CDyMPvVMw.png)
  
## RadiusSearch
RadiusSearch is the Search algorithm designed for RadiusAgent.

User provides distance/radius , location(latitude , longitude) , Range of budget(min-max), Range of bedroom(min-max),Range of bathroom(min-max).

The received value is passed to "driverFunction" which adds min-max of budget, bedroom, and bathroom to there respective objects and cases where only one value of the present is handled.

Values are then passed to "obtainDataFromDb_and_Validate" function. As the name suggests this function gets a from the propertytable from radiusagent DB.After making the connection with the DB the "obtainDataFromDb_and_Validate" function with the help of "validate" shortlist the property which is less than 10 miles also falls +/-25% of the budget range and +/-2 of the bedroom and bathroom range.

The property with distance value which is less than 2 is given a score of 40 in the match.

The properties which fall under these criteria are added to an ArrayList and sent to for requirement match to "scoreMatch" function.

scoreMatch function uses "budget_parameter" , "bedroom_parameter" and "bathroom_parameter" , addes the score the each property one by one.

After the match is complete the all the match which are more than 40 on the scorecard is displayed.
The result can also be passed as ArrayList for other purposes.

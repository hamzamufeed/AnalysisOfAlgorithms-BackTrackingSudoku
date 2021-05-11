# AnalysisOfAlgorithms-BackTrackingSudoku

Like all other Backtracking problems, Sudoku can be solved by one by one assigning numbers to empty cells.
Before assigning a number, check whether it is safe to assign. Check that the same number is not present 
in the current row, current column and current 3X3 sub grid. After checking for safety, assign the number, 
and recursively check whether this assignment leads to a solution or not.If the assignment doesn’t lead to 
a solution, then try the next number for the current empty cell. And if none of the number (1 to 9) leads 
to a solution, return false and print no solution exists.
More features has been added such as generating random problems to solved with Easy or Hard difficulty, and
these patterns can be saved to file if the user want to play it later.
The ability to check the current solution and highlight the feild with wrong numbers.
If the user can't solve it, there's an option to show the final solution.
There's two counts shown for the user: a counter for chnages in the pattern, and a counter for number of times 
that the user checked his/her solution.

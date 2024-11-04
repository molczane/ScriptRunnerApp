I copied task deskroption here, so I'll attach a picture/description to every point mentioned.

# Develop a GUI tool that allows users to enter a script, execute it, and see its output side-by-side. 

You may choose between Swift and Kotlin as supported scripting languages. You are free to use any modern mainstream programming language you are comfortable with for the implementation. (The reviewers will be most familiar with Swift, Kotlin and Java.) The solution should be a link to a git repository or Zip archive. Please provide instructions to build and run your tool from the provided sources and other necessary information in a README file.
Screen shots or a recording are appreciated, especially if your project will not run on macOS.

Please be prepared to demonstrate and explain the code during the interview.

Required functionality
- Should have an editor pane and an output pane.

Write the script to a file and run it using "/usr/bin/env swift foo.swift", or "kotlinc -script foo.kts" respectively. - Assume the script might run for a long time.
Show live output of the script as it executes. - Show errors from the execution/if the script couldn’t be interpreted.
Show an indication whether the script is currently running.
- Show an indication whether the exit code of the last run was non-zero.
  Implement at least one of the following:

Highlight language keywords in a color different from the rest of the code. You may assume that keywords are not valid in other contexts, e.g. as enum member names. You may restrict yourself to 10 keywords, if more could be added easily.
Make location descriptions of errors (e.g. “script:2:1: error: cannot find 'foo' in scope”) clickable, so users can navigate to the exact cursor positions in code.
We're not looking for a standard solution. Feel free to make it unique by focusing on areas you care about.

Alternative
If you lack the time for this task, you may provide us with an existing project with similar complexity and focus area or link to non-trivial contributions in open-source projects.

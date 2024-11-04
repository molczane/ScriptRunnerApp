# Script Runner App - Extending Swift Support for Fleet internship application task

I copied task description here, so I'll attach a picture/description to every point mentioned.

1. Develop a GUI tool that allows users to enter a script, execute it, and see its output side-by-side. 

This is how my app looks like. It has code editor and output panel. Pretty straightforward.

<img width="1469" alt="app_overlook" src="https://github.com/user-attachments/assets/9e7546ea-e514-48b2-962e-795e955a1952">

2. You may choose between Swift and Kotlin as supported scripting languages.

I have decided to have switch. If the language background is blue, then it's chosen scripting language.

<img width="1469" alt="app_overlook" src="https://github.com/user-attachments/assets/cb5e4b59-e073-4153-8859-ad8918fbee07">

3. You are free to use any modern mainstream programming language you are comfortable with for the implementation. (The reviewers will be most familiar with Swift, Kotlin and Java.)

For project implementation I've chosen Kotlin Multiplatform technology with Compose Multiplatform UI framework. I really enjoy this technology, thus my decision.

4. The solution should be a link to a git repository or Zip archive. Please provide instructions to build and run your tool from the provided sources and other necessary information in a README file. Screen shots or a recording are appreciated, especially if your project will not run on macOS.

It is.

## Required functionality: 
- Should have an editor pane and an output pane.

Done, as shown on first picture.

- Write the script to a file and run it using "/usr/bin/env swift foo.swift", or "kotlinc -script foo.kts" respectively.

Exactly this happens when user presses green arrow button to run the script. 

<img width="1470" alt="Zrzut ekranu 2024-11-4 o 22 59 40" src="https://github.com/user-attachments/assets/4fbc2511-eaa8-40f0-b3a5-fa265f444f68">

- Assume the script might run for a long time.

It's taken into account. User Can also stop script from executing using red square stop button.

<img width="1468" alt="Zrzut ekranu 2024-11-4 o 23 01 38" src="https://github.com/user-attachments/assets/1b10bc77-e100-437c-9764-31dd5f1603bf">

https://github.com/user-attachments/assets/d01adb0b-2ef1-4c15-95d0-497b3e9470f2

Show live output of the script as it executes. - Show errors from the execution/if the script couldn’t be interpreted.
Show an indication whether the script is currently running.
- Show an indication whether the exit code of the last run was non-zero.
  Implement at least one of the following:

Highlight language keywords in a color different from the rest of the code. You may assume that keywords are not valid in other contexts, e.g. as enum member names. You may restrict yourself to 10 keywords, if more could be added easily.
Make location descriptions of errors (e.g. “script:2:1: error: cannot find 'foo' in scope”) clickable, so users can navigate to the exact cursor positions in code.
We're not looking for a standard solution. Feel free to make it unique by focusing on areas you care about.

Alternative
If you lack the time for this task, you may provide us with an existing project with similar complexity and focus area or link to non-trivial contributions in open-source projects.

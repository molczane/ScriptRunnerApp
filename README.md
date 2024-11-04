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

- Show live output of the script as it executes.

It works, I tested my app on fibanacci numbers generating script.

https://github.com/user-attachments/assets/d01adb0b-2ef1-4c15-95d0-497b3e9470f2

- Show errors from the execution/if the script couldn’t be interpreted.

It's working. In this video example, I changed scripting language to Swift while having Kotlin code. Errors were shown.

https://github.com/user-attachments/assets/70ff0cbe-bef8-4ee0-8c14-09a61ae52200

- Show an indication whether the script is currently running.

I created little pulsing dot, that is indicating whether script is running. It was visible on previous videos.

<img width="1468" alt="Zrzut ekranu 2024-11-4 o 23 10 10" src="https://github.com/user-attachments/assets/861a0f26-f283-4826-8307-86eb9ee230c9">

- Show an indication whether the exit code of the last run was non-zero.

If the exit code of the last run was non-zero, pulsing dot is becoming red.

<img width="1470" alt="Zrzut ekranu 2024-11-4 o 23 11 50" src="https://github.com/user-attachments/assets/5087248a-3514-4a46-aa07-c22d966c33d8">

# Implement at least one of the following:

1. Highlight language keywords in a color different from the rest of the code. You may assume that keywords are not valid in other contexts, e.g. as enum member names. You may restrict yourself to 10 keywords, if more could be added easily.

Language keyword are highlighted in blue, as was visible in the previous pictures.

Make location descriptions of errors (e.g. “script:2:1: error: cannot find 'foo' in scope”) clickable, so users can navigate to the exact cursor positions in code.
We're not looking for a standard solution. Feel free to make it unique by focusing on areas you care about.

I had problem implementing that. Because, list of errors is creating properly. I want to make every first line of error clickable. But for some reason only very first line is clickable. I show it on video.

https://github.com/user-attachments/assets/270074c3-6f4f-40ef-aa92-9aee2225ca6b

So as shown on video, this functionality works. But not entirely well. I suppose it's some Compose Multiplatform related problem. But I've run out of time to degub this issue.


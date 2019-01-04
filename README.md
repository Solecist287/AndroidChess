# AndroidChess
This is an android chess app that allows up to two people to play on a single phone.
## Installation
Clone this repository into some directory and import it into **Android Studio** as an existing project.
```
git clone https://github.com/Solecist287/AndroidChess.git
```
Only supports Nexus 4 size for now.
Only supports Android Pie.
## Tools Used
* Java 8
* Android Studio
## Features
* Basic moves
    * Including en passant and castling
* Resign
* Draw
* Check warnings, checkmate, stalemate
* AI can choose random move
* Save games and replay them one move at a time
## How to use
### Moving pieces
Pieces can be selected by touching their current location, which highlights them, and then are moved by tapping their valid destination.
### Resigning
Players can resign at any time by clicking the **resign button**, which then prompts the player to choose whether or not to save the game. Resigning counts as a win for the other player.
### Drawing
The game can end in a draw if one player sends a draw request, by clicking the **draw button**, and the other accepts it, by clicking the **draw button immediately after**.
### Choosing a random move for a player's turn
Clicking the **AI button** chooses a random move for the current player's turn.
### Saving a game
When a game terminates in any fashion, a prompt appears on the screen. The player must enter a **non-empty** name for the recorded game and click the **OK button**. After having entered a name for the game, it is saved to an internal text file called "games.txt" which holds all past saved games.
### Replaying a game
Clicking on the **recorded games button** shows the list of previously recorded games. After clicking a game on the list, the user is transported to a screen where they can use **prev move button** and **next move button** to see the recorded moves in action on the chessboard.

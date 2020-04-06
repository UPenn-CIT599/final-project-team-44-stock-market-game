# MCIT_591_Stock_Market
Our team’s final project involves developing a stock trading application, providing the user the ability to provide input on the stocks they would like to purchase/sell based on available funds in their account.

# Project Description
Our stock market “paper trading” program will include the following key features:
- Users will initially invest $X amount of fake money or upload a portfolio file to the program and the game will track the performance of their investment(s). Portfolio performance and position data will be able to be updated day over day.
- Investments will be limited to long-only publicly traded US equities.  Users will not be able to invest in bonds, options, or any other vehicle outside of publicly traded US stocks and will not be able to short any stock.
- The program will incorporate a link to an API that will obtain real-time stock prices, which will be used to simulate trades and update position data accordingly in real time.

# Key Classes
- PositionFileIO: This will load in the user's portfolio from a csv file or create a new csv file to track the user's portfolio. Trades will take place within the program and update the portfolio. When the user is done with their trading sessoin, their updated portfolio is written back out to their portfolio file to be used for the next trading session or trading day.

- Position: The Position object takes in data related to the position (symbol, shares, average cost) and calculates some position economics (cost basis, current market value, position return). These positions will be grouped in the portfolio object and updated throughout the program as trades are completed.

- Portfolio: The portfolio object is a grouped set of positions. When trades are completed in the program, the portfolio is updated accordingly (cash is adjusted, positions are created, deleted, updated based upon the trade taking place). The portfolio is loaded in from the csv file at the start of the program and 

- Trade: The trade class will be used to interact with the user and get the trade instructions. The trade instructions will then permeate throughout the program, updating postiions and the portfolio accordingly.

- YahooQuote: This class uses yahoo finance as a data source and pulls the current price of a stock in to the program. The current price wlil be used to update positions and the portfolio accordingly.

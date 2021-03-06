package com.webcheckers.ui;

import com.webcheckers.appl.GameManager;
import com.webcheckers.appl.PlayerLobby;
import com.webcheckers.model.CheckersGame;
import com.webcheckers.model.Player;
import com.webcheckers.model.TournamentScoreboard;
import spark.*;

import java.util.Objects;

import static spark.Spark.halt;

/**
 * UI Controller to GET the sign out route
 */
public class GetSignOutRoute implements Route {

    private final PlayerLobby playerLobby;
    private final GameManager gameManager;

    /**
     * Initializes the GetSignOutRoute
     *
     * @param playerLobby - player lobby used to access players
     * @param gameManager - game manager used to access games
     */
    GetSignOutRoute(final PlayerLobby playerLobby, final GameManager gameManager) {
        Objects.requireNonNull(playerLobby, "Player Lobby must not be null");
        Objects.requireNonNull(gameManager, "Game Manager must not be null");
        this.playerLobby = playerLobby;
        this.gameManager = gameManager;
    }

    /**
     * Removes the player from the player lobby and destroys the game
     *
     * @param request  - the HTTP request
     * @param response - the HTTP response
     * @return - null
     */
    @Override
    public Object handle(Request request, Response response) {
        Player player = request.session().attribute("Player");
        String playerName = player.getName();
        Player.GameType type = player.getType();

        CheckersGame game = gameManager.getGame(player);

        playerLobby.destroyPlayer(playerName);

        if(player.getType() == Player.GameType.TOURNAMENT)
            TournamentScoreboard.removePlayer(player);

        if (!playerLobby.isPlayerInLobby(player)) {
            // Remove the player from the session
            request.session().removeAttribute(playerName);
            player = null;
            if(game != null) {
                gameManager.resignGame(new Player(playerName, type));
            }
        }
        // Redirect to homepage which should show the Signed Out page
        response.redirect(WebServer.HOME_URL);

        return null;
    }
}

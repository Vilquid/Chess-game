console.log("started javascript loading");

// time elapsed

$(document).ready(function() {
    let counterGame = parseInt($("#time")[0].innerText);
    let counterMove = parseInt($("#time_move")[0].innerText);
    let status = $("#game_status")[0].innerText;
    let interval = setInterval(function() {
        if (status === "in progress") {
            counterGame++;
            counterMove++;

            $('#time').text(counterGame);
            $('#time_move').text(counterMove);
        }
    }, 1000);
});

// board component
let board = $("#board");


/**
 * Manage drag and drop
 */
$(function (e) {
    // replace text for the player's turn.
    let turnText = "Unknow";
    if ($('#mate').html() == null && $('#winner').html() == null)
        turnText = $('#player-turn').html();

    $('.figure').draggable({
        containment: board,
        zIndex: 1,
        snap: ".cell",
        revert: 'invalid'
    });

    $('.cell').droppable({
        accept: function (el) {
            return el.hasClass(turnText.toLowerCase()) && ($('#mate').length <= 0);
        },
        tolerance: 'intersect',
        over: function () {
            $(this).css("background-color", "green");
            console.log($(this).attr('class'));
        },
        out: function () {
            $(this).css("background-color", "")
        },
        drop: function (event, ui) {
            let dropped = ui.draggable;
            let droppedOn = $(this);

            let gameId = $(location).attr('href').split('/')[5]; // http://localhost:8080/game/play/{id}

            let deleteUrl;
            const oldPawnId = $(droppedOn).children();
            const oldClass = $(this).attr('class').indexOf('disabled');

            $(droppedOn).css("background-color", "");
            $(this).addClass('disabled');
            $(dropped).parent().removeClass('disabled');
            $(dropped).parent().droppable("enable");
            $(dropped).detach().css({top: 0, left: 0}).appendTo(droppedOn);
            let newPawnId = $(dropped).attr('data-id');

            let x = $(droppedOn).attr('data-x');
            let y = $(droppedOn).attr('data-y');
            let moveUrl = '/game/move/' + gameId + '/' + newPawnId + '/' + x + '/' + y;


            // create link to update a pawn's position
            // if the cell is not empty
            if (oldClass > 0) {
                // Create link to delete
                deleteUrl = '/game/move/' + gameId + '/' + newPawnId + '/' + oldPawnId.attr('data-id');
                // Delete the piece of the screen
                oldPawnId.remove();
                // Delete the piece in the database
                window.location.href = deleteUrl;
            } else {
                // update the pawn's position
                window.location.href = moveUrl;
            }
        },
    });
});

if ($(location).attr('href').split('/')[4] === "promote") {
    $('#promotionModal').modal('toggle');
}

$(function () {
    if ($('#mate').html() != null && $('#winner').html() == null) {
        let currentPlayer = $('#player-turn').html();
        let winner = (currentPlayer === "Black" ? $('#Player1').text() : $('#Player2').text());
        let looser = (currentPlayer === "Black" ? $('#Player2').text() : $('#Player1').text());
        let gameId = $(location).attr('href').split('/')[5]; // http://localhost:8080/game/play/{id}
        window.location.href = '/game/endgame/' + gameId + '/' + winner + '/' + looser;
    }
});


function leaveGame() {
    let gameId = $(location).attr('href').split('/')[5]; // http://localhost:8080/game/play/{id}
    let looser = $('#leave').attr('data-id');
    let winner;

    if($('#Player1').text() === looser) {
        winner = $('#Player2').text();
    }
    else {
        winner = $('#Player1').text();
    }
    window.location.href = '/game/endgame/' + gameId + '/' + winner + '/' + looser;
}

console.log("js fully loaded");
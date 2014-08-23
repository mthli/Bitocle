$(function(){
    $('pre code').each(function(){
        var lines = $(this).text().split('\n').length - 1;
        var $numbering = $('<ul/>').addClass('pre-numbering');
        $(this)
        .addClass('has-numbering')
        .parent()
        .append($numbering);
        for(i=1;i<=lines;i++){
            $numbering.append($('<li/>').text(i));
        }
    });
});

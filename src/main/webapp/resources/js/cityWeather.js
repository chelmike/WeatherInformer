$(document).ready(function () {
    $('#informer').val($('#currInformer').val());
    $('#city').val($('#currCity').val());

    $(".error-title").click(function () {
        $(".error-message").toggle();
    });

    setTimeout(function(){ top.location.reload(); }, 300000);
});


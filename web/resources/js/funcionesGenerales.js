$(document).ready(function () {
    $.cookie("primefaces.download", null);

    $('.navbar-toggle').click(function () {
        $('.navbar-nav').toggleClass('slide-in');
        $('.side-body').toggleClass('body-slide-in');
        $('#search').removeClass('in').addClass('collapse').slideUp(200);

        /// uncomment code for absolute positioning tweek see top comment in css
        //$('.absolute-wrapper').toggleClass('slide-in');
        
    });
      
});

function muestraMensajeDescargaExcel(idMensajes) {
    PF(idMensajes).renderMessage({"summary": "La descarga se ha comenzado",
        "detail": "La descarga se realizar\u00e1 en un momento, se est\u00e1 procesando la informaci\u00f3n",
        "severity": "info"});
}

function muestraMensajeCargarExcel(idMensajes) {
    PF(idMensajes).renderMessage({"summary": "La carga de informaci\u00f3n ha comenzado",
        "detail": "Por favor espere, el proceso puede tardar unos minutos",
        "severity": "info"});
}

function showBlockDescargaExcel(nombreBlock) {
    PF(nombreBlock).show();
}

function hideBlockDescargaExcel(nombreBlock) {
    PF(nombreBlock).hide();
    borraTempExcel();
}

function myMonitorDownload(d, c, nombreBlock) {
    if (d) {
        d(nombreBlock);
    }
    var interval = setInterval(function () {
        var value = $.cookie("primefaces.download");
        if (value === "true") {
            if (c) {
                c(nombreBlock);
            }
            $.cookie("primefaces.download", null);
            clearInterval(interval);
        }
    }, 250);
}

function asignaListenerSlider() {
    $(".previous").on('click', function (e) {
        e.preventDefault();
    });
    $(".next").on('click', function (e) {
        e.preventDefault();
    });
}

function refrescaSlider() {
    $('.crsl-items').carousel({
        visible: 1,
        itemMinWidth: 220,
        itemEqualHeight: 160,
        itemMargin: 9
    });
}

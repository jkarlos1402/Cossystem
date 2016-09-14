$(document).ready(function () {
    $.cookie("primefaces.download", null);
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

function showBlockDescargaExcel() {
    PF("blockTabla").show();
}

function hideBlockDescargaExcel() {
    PF("blockTabla").hide();
    stop();
}

function myMonitorDownload(d, c) {
    if (d) {
        d();
    }
    var interval = setInterval(function () {
        var value = $.cookie("primefaces.download");
        console.log(value);
        if (value === "true") {
            if (c) {
                c();
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

    console.log("ya se actualizo slider");
}

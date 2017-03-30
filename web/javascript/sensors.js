/**
 * Created by vlad on 19.03.2017.
 */
var sensors = [];
$.ajax({
    type: "GET",
    url: "http://localhost:18080/sensor/set",
    params: {
        location: "Spital Moinesti"
    },
    success: function(r) {
        sensors = JSON.parse(r);
        setOptions();
        setSerialNo(document.getElementById("tip_senzor"));
    }
});

function setOptions() {
    var tipSelect = document.getElementById("tip_senzor");
    for (var i = 0; i < sensors.length; ++i) {
        var tipOption = document.createElement("option");
        tipOption.value = sensors[i]["_id"];

        tipOption.innerHTML = sensors[i]["_id"];
        tipSelect.appendChild(tipOption);
    }
}

function setSerialNo(selectObject) {
    var selectedValue = selectObject.value;
    var selectedTypeIndex = -1;

    for (var i = 0; i < sensors.length; ++i) {
        if (sensors[i]["_id"] == selectedValue) {
            selectedTypeIndex = i;
        }
    }
    var serialNos = sensors[selectedTypeIndex]["serial_no"];

    var serialNoSelect = document.getElementById("serial_no");
    serialNoSelect.innerHTML = '';

    for (var i = 0; i < serialNos.length; ++i) {
        var serialNoOption = document.createElement("option");
        serialNoOption.value = serialNos[i];
        serialNoOption.innerHTML = serialNos[i];
        serialNoSelect.appendChild(serialNoOption);
    }
}

function setSensor() {
    var serialNo = document.getElementById("serial_no").value;
    var patientDn = document.getElementById("patient_dn").value;

    $.ajax({
        type: "POST",
        url: "http://localhost:18080/sensor/set",
        params: {
            serialNo: serialNo,
            patientDn: patientDn
        },
        success: function(r) {
            console.log(r);
        }
    });
}
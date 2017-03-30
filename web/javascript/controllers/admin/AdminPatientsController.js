/**
 * Created by vlad on 30.03.2017.
 */
MainModule.controller("AdminPatientsController", ['$scope', '$http', 'SensorsServices', function($scope, $http, sensorsServices) {
    $scope.patients = [];
    $scope.patientInfo = "";
    $scope.inactiveSensors = "";
    $scope.inactiveSensorsType = "";
    $scope.currentDisplayedInactiveSensors = "";
    $scope.inactiveSensorsStuff = {
        sensorType: "",
        serial_no: null
    };

    $scope.newPatient = {
        distinguishedName: "",
        fullName: ""
    };

    $scope.patientToDelete = {
        distinguishedName: "",
        fullName: ""
    };

    initPatients();

    function initPatients() {
        $http({
            method: "GET",
            url: "/patients",
            params: {
                filter: "all"
            }
        }).then(function(response) {
            $scope.patients = response.data;
        });
    }

    $scope.addPatient = function() {
        $http({
            method: "POST",
            url: "/patients",
            data: $scope.newPatient
        }).then(function(response) {
            console.log(response);
            window.location.reload();
            alert("Pacient adaugat cu succes!");
        }, function(response) {
            alert("Eroare la adaugarea pacientului!");
        })
    };

    $scope.removePatient = function() {
        $http({
            method: "POST",
            url: "/patient/remove",
            params: {
                patientDn: $scope.patientToDelete.distinguishedName
            }
        }).then(function(response) {
            console.log(response);
            window.location.reload();
            alert("Pacient eliminat cu succes!");
        }, function(response) {
            alert("Eroare la eliminarea pacientului!");
        })
    };

    $scope.getCompletePatientInfo = function(patient) {
        $http({
            method:"GET",
            url:"/patient/show",
            params: {
                patientDn: patient.distinguishedName
            }
        }).then(function(response) {
            $scope.patientInfo = response.data;
            sensorsServices.getActiveSensors(patient).then(function(response) {
                console.log(response);
                $scope.patientInfo.sensors = response.data;
            });
            sensorsServices.getInactiveSensors(patient).then(function(response) {
                console.log(response);
                $scope.inactiveSensorTypes = response.data.map(function(elem) {
                    return elem._id;
                });

                $scope.inactiveSensors = response.data;
            })
        })
    };

    $scope.setDisplayedInactiveSensors = function() {
        $scope.currentDisplayedInactiveSensors = $scope.inactiveSensors.find(function(elem) {
            return elem._id == $scope.inactiveSensorsStuff.sensorType;
        }).serial_no;
    };

    $scope.setSensor = function() {
        if ($scope.inactiveSensorsStuff.serial_no != null) {
            sensorsServices.setSensor($scope.inactiveSensorsStuff.serial_no,
                $scope.patientInfo.distinguishedName).then(function (response) {
                alert("Senzor adaugat cu succes!");
                resetSensorOptions();
                $scope.getCompletePatientInfo($scope.patientInfo);
            }, function (response) {
                alert("Eroare la adaugarea senzorului!");
                resetSensorOptions();
                $scope.getCompletePatientInfo($scope.patientInfo);
            });
        } else {
            alert("Selectati modelul si seria senzorului!");
        }
    };

    function resetSensorOptions() {
        $scope.inactiveSensorsStuff.serial_no = null;
        $scope.currentDisplayedInactiveSensors = [];
    }

    $scope.removeSensor = function(serial_no) {
        sensorsServices.removeSensor(serial_no).then(function(response) {
            alert("Sensor eliminat cu succes!");
            $scope.getCompletePatientInfo($scope.patientInfo);
        }, function(response) {
            alert("Eroare la eliminarea senzorului!");
            $scope.getCompletePatientInfo($scope.patientInfo);
        });
    };


}]);
/**
 * Created by vlad on 27.03.2017.
 */
MainModule.factory("SensorsServices", ['$http', function($http) {
    function getActiveSensors(patient) {
        return $http({
            method: "GET",
            url: "/sensors",
            params: {
                status: "active",
                patientDn: patient.distinguishedName
            }
        })
    }

    function getInactiveSensors(patient) {
        return $http({
            method: "GET",
            url: "/sensors",
            params: {
                status: "inactive",
                location: "Spital Moinesti"
            }
        })
    }

    function setSensor(serial_no, patientDn) {
        return $http({
            method:"POST",
            url: "/sensors/set",
            params: {
                serial_no: serial_no,
                patient_dn: patientDn
            }
        })
    }

    function removeSensor(serial_no) {
        return $http({
            method:"POST",
            url: "/sensors/remove",
            params: {
                serial_no: serial_no
            }
        })
    }


    return {
        getActiveSensors: getActiveSensors,
        getInactiveSensors: getInactiveSensors,
        setSensor: setSensor,
        removeSensor: removeSensor
    }
}]);
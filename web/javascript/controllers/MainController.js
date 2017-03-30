/**
 * Created by vlad on 27.03.2017.
 */
MainModule.controller("MainController", ['$scope', '$http', function($scope, $http) {
    $scope.authenticationData = {
        uid: "",
        password : ""
    };

    $scope.login = function() {
        $http({
            method:"POST",
            url:"/login",
            params:{
                uid: $scope.authenticationData.uid,
                password: $scope.authenticationData.password
            }
        }).then(function(response) {
            alert("Logged in successfully!");
            console.log(response);
            window.location = response.data;
        }, function(response){
            alert("Failed to login! Invalid credentials!");
        })
    }
}]);
/**
 * Created by vlad on 05.04.2017.
 */
MainModule.controller("CommandsController", ['$scope', '$http', function($scope, $http) {
    $scope.commands = [];

    $scope.getRowColor = function(command) {
        switch (command.severity) {
            case "NORMAL":
                return "active";
            case "IMPORTANT":
                return "danger";
        }
    };

    $scope.getCommands = function() {
        $http.get("/commands").then(function(response) {
            console.log(response);
            $scope.commands = response.data;
        });
    };

    $scope.resolve = function(command) {
        $http({
            method: "POST",
            url:"/resolve_command",
            params: {
                commandId: command["_id"]
            }
        }).then(function (response){
            console.log(response);
        })
    };

    $scope.getCommands();
}]);
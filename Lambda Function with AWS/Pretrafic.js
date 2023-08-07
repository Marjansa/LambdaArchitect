'use strict';

const AWS = require('aws-sdk');

const codedeploy = new AWS.CodeDeploy({ apiVersion: '2014-10-06' });

const lambda = new AWS.Lambda();

exports.handler = async (event) => {

    console.log("Entering PostTraffic Hook!");

    // Read the DeploymentId and LifecycleEventHookExecutionId from the event payload

    const deploymentId = event.DeploymentId;

    const lifecycleEventHookExecutionId = event.LifecycleEventHookExecutionId;

    const functionToTest = process.env.NewVersion;

    console.log("AftereAllowTraffic hook tests started");

    console.log("Testing new function version: " + functionToTest);

    // Create parameters to pass to the updated Lambda function that

    // include the newly added "time" option. If the function did not

    // update, then the "time" option is invalid and function returns

    // a statusCode of 400 indicating it failed.

    const lambdaParams = {

        FunctionName: functionToTest,

        Payload: "{\"quantity\": 200}",

        InvocationType: "RequestResponse"

    };

    let lambdaResult = "Failed";

    // Invoke the updated Lambda function.

    const lambdaResponse = await lambda.invoke(lambdaParams).promise();

    console.log(JSON.stringify(lambdaResponse))

    if (lambdaResponse.statusCode != "400") {

        console.log("Validation succeeded");

        lambdaResult = "Succeeded";

    }

    else {

        console.log("Validation failed");

    }

    // Complete the PreTraffic Hook by sending CodeDeploy the validation status

    const params = {

        deploymentId: deploymentId,

        lifecycleEventHookExecutionId: lifecycleEventHookExecutionId,

        status: lambdaResult // status can be 'Succeeded' or 'Failed'

    };

    // Pass CodeDeploy the prepared validation test results.

    const codeDeployResponse = await codedeploy.putLifecycleEventHookExecutionStatus(params).promise();

    console.log("CodeDeploy status updated successfully");

}
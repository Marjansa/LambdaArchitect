import uuid

import random

import boto3

dynamodb = boto3.resource('dynamodb')

table = dynamodb.Table('event_details')

location = ['SFO', 'London', 'HongKong', 'NewYork', 'Seattle']

def lambda_handler(event, context):    

    event_id = str(uuid.uuid4())

    event_location = random.choice(location)

    number_of_tickets = random.randint(50,1000)

    cost = random.randint(10,1000)   

    response = table.put_item(

           Item={

                'event_id': event_id,

                'event_location': event_location,

                'number_of_tickets': number_of_tickets,

                'cost': cost,

                'currency': 'USD'

            }

        )    

    return response
import uuid

def lambda_handler(event, context):    

    if event['quantity'] < 100:

        discount_code = str(uuid.uuid4())

    else:

        discount_code = 'premium-' + str(uuid.uuid4())

    return discount_code
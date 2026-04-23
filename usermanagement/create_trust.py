import json
data = {'Version':'2012-10-17','Statement':[{'Effect':'Allow','Principal':{'Service':'ecs-tasks.amazonaws.com'},'Action':'sts:AssumeRole'}]}
with open('trust-policy.json','w') as f:
    json.dump(data,f,indent=2)
print('Done')

:- module(spec, [(trace_expression/2), (match/2)]).
:- use_module(monitor(deep_subdict)).
:- use_module(library(clpr)).
match(_event, pub_et(PubId, Topic, MsgId)) :- deep_subdict(_event, _{agent:"pub", operation:"send", id:PubId, topic:Topic, msgId:MsgId}).
match(_event, subs_et(SubId, Topic)) :- deep_subdict(_event, _{agent:"sub", operation:"subscription", id:SubId, topic:Topic}).
match(_event, recv_et(SubId, Topic, MsgId, PubId)) :- deep_subdict(_event, _{agent:"sub", operation:"receive", id:SubId, topic:Topic, msgId:MsgId, sender:PubId}).
match(_event, newPub_et(PubId)) :- deep_subdict(_event, _{agent:"pub", operation:"new", id:PubId}).
match(_event, unsubs_et(SubId, Topic)) :- deep_subdict(_event, _{agent:"sub", operation:"unsubscription", id:SubId, topic:Topic}).
match(_event, delPub_et(PubId)) :- deep_subdict(_event, _{agent:"pub", operation:"del", id:PubId}).
match(_event, pub_et(PubId)) :- match(_event, pub_et(PubId, _, _)).
match(_event, pub_et) :- match(_event, pub_et(_)).
match(_event, notNewOrDelPub_et(PubId)) :- not(match(_event, newPub_et(PubId))),
	not(match(_event, delPub_et(PubId))).
match(_event, recv_et(SubId, Topic)) :- match(_event, recv_et(SubId, Topic, _, _)).
match(_event, recv_et) :- match(_event, recv_et(_, _, _, _)).
match(_event, subs_et) :- match(_event, subs_et(_, _)).
match(_event, notSubs_et) :- not(match(_event, subs_et)).
match(_event, notSubsOrUnsubs_et(SubId, Topic)) :- not(match(_event, subs_et(SubId, Topic))),
	not(match(_event, unsubs_et(SubId, Topic))).
match(_event, unsubs_et) :- match(_event, unsubs_et(_, _)).
match(_event, subsOrRecvOrUnsubs_et) :- match(_event, subs_et).
match(_event, subsOrRecvOrUnsubs_et) :- match(_event, recv_et).
match(_event, subsOrRecvOrUnsubs_et) :- match(_event, unsubs_et).
match(_event, newPub_et) :- match(_event, newPub_et(_)).
match(_event, notNewPub_et(PubId)) :- not(match(_event, newPub_et(PubId))).
match(_event, notNewPub_et) :- not(match(_event, newPub_et)).
match(_event, delPub_et) :- match(_event, delPub_et(_)).
match(_event, newPubOrPubOrDelPub_et) :- match(_event, newPub_et).
match(_event, newPubOrPubOrDelPub_et) :- match(_event, pub_et).
match(_event, newPubOrPubOrDelPub_et) :- match(_event, delPub_et).
match(_event, involve_et(PubId, SubId, Topic)) :- match(_event, pub_et(PubId, Topic, _)).
match(_event, involve_et(PubId, SubId, Topic)) :- match(_event, recv_et(SubId, Topic, _, PubId)).
match(_event, involve_et(PubId, SubId, Topic)) :- match(_event, unsubs_et(SubId, Topic)).
match(_event, relevant_et) :- match(_event, pub_et).
match(_event, relevant_et) :- match(_event, subs_et).
match(_event, relevant_et) :- match(_event, recv_et).
match(_event, relevant_et) :- match(_event, newPub_et).
match(_event, any_et) :- deep_subdict(_event, _{}).
match(_event, none_et) :- not(match(_event, any_et)).
trace_expression('Main', Main) :- (Main=((relevant_et>>(((((NoMultipleSubs/\NoMultipleNewPub)/\((subsOrRecvOrUnsubs_et>>SubsThenRecv);1))/\((newPubOrPubOrDelPub_et>>NewPubThenPub);1))/\CheckSubs)/\CheckPub));1)),
	(NoMultipleSubs=(star(notSubs_et)*optional(var(topic, var(subId, (subs_et(var(subId), var(topic))*((star(notSubsOrUnsubs_et(var(subId), var(topic)))*(unsubs_et(var(subId), var(topic))*1))/\NoMultipleSubs))))))),
	(NoMultipleNewPub=(star(notNewPub_et)*optional(var(pubId, (newPub_et(var(pubId))*((star(notNewOrDelPub_et(var(pubId)))*(delPub_et(var(pubId))*1))/\NoMultipleNewPub)))))),
	(SubsThenRecv=optional(var(topic, var(subId, (subs_et(var(subId), var(topic))*((star(recv_et(var(subId), var(topic)))*unsubs_et(var(subId), var(topic)))|SubsThenRecv)))))),
	(NewPubThenPub=optional(var(pubId, (newPub_et(var(pubId))*((star(pub_et(var(pubId)))*del_et(var(pubId)))|NewPubThenPub))))),
	(CheckSubs=(star(notSubs_et)*optional(var(subId, var(topic, (subs_et(var(subId), var(topic))*(app(GenCheckSubs, [var(subId), var(topic)])/\CheckSubs))))))),
	(GenCheckSubs=gen([subId, topic], optional((((unsubs_et(var(subId), var(topic))*1)\/(notNewPub_et*app(GenCheckSubs, [var(subId), var(topic)])))\/var(pubId, (newPub_et(var(pubId))*(((involve_et(var(pubId), var(subId), var(topic))>>app(Queue, [var(pubId), var(subId), var(topic)]));1)/\app(GenCheckSubs, [var(subId), var(topic)])))))))),
	(CheckPub=(star(notNewPub_et)*optional(var(pubId, (newPub_et(var(pubId))*(app(GenCheckPub, [var(pubId)])/\CheckPub)))))),
	(GenCheckPub=gen([pubId], optional((((delPub_et(var(pubId))*1)\/(notSubs_et*app(GenCheckPub, [var(pubId)])))\/var(topic, var(subId, (subs_et(var(subId), var(topic))*(((involve_et(var(pubId), var(subId), var(topic))>>app(Queue, [var(pubId), var(subId), var(topic)]));1)/\app(GenCheckPub, [var(pubId)]))))))))),
	(Queue=gen([pubId, subId, topic], optional((unsubs_et(var(subId), var(topic))\/var(msgId, (pub_et(var(pubId), var(topic), var(msgId))*((recv_et|app(Queue, [var(pubId), var(subId), var(topic)]))/\((recv_et>>(recv_et(var(subId), var(topic), var(msgId), var(pubId))*1));1)))))))).

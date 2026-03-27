Below is an **updated README.md** section with **production-level improvements added** while keeping your original structure intact.

You can directly replace your README with this version.

---

# Observability Alerting System (Spring Boot + PostgreSQL)

## Overview

This project is a simplified **production-style alerting and on-call management system** built using **Spring Boot (Java 17)** and **PostgreSQL**.

The system continuously evaluates metrics (CPU, error rate, memory etc.) and triggers alerts when predefined rules are violated for a configured duration. Each alert is linked to a **runbook** that explains how engineers should resolve the issue.

Although this project uses simplified logic and simulated metrics, the architecture and flow closely resemble real production monitoring systems such as:

* Prometheus + AlertManager
* Datadog Monitors
* AWS CloudWatch Alarms
* Grafana Alerting
* New Relic Alerts

The project demonstrates how backend engineers design **reliable systems that detect failures early and reduce downtime**.

---

# High Level Architecture

Metrics Source → Rule Evaluation → Alert Creation → Deduplication → Notification Routing → On-call Assignment → Runbook Execution → Alert Resolution

---

# Core Concepts

## 1. Metrics

Metrics represent system health signals.

Examples:

* cpu_usage
* memory_usage
* error_rate
* response_time

In this project metrics are simulated using hardcoded values.

### Production Reality

In production, metrics are automatically collected from:

* application services
* Kubernetes pods
* databases
* queues
* cloud infrastructure
* JVM runtime

Common tools:

* Micrometer (Spring Boot)
* Prometheus
* Datadog Agent
* AWS CloudWatch
* OpenTelemetry

Metrics are stored in **time-series databases**.

Example production metric:

```
cpu_usage{service="payment-service", env="prod", region="ap-south-1"}
```

Labels/tags allow filtering metrics per service or environment.

---

## 2. Alert Rules

Alert rules define conditions that represent problems.

Example rule:

cpu_usage > 80 for 60 seconds

Rule structure:

| field            | meaning                   |
| ---------------- | ------------------------- |
| name             | human readable alert name |
| metric_name      | metric being evaluated    |
| threshold        | threshold value           |
| condition        | GT, LT, GTE, LTE          |
| duration_seconds | violation duration        |
| severity         | INFO, WARNING, CRITICAL   |
| runbook_id       | linked resolution steps   |
| labels           | metadata (service, env)   |
| cooldown_seconds | prevent alert spam        |

Production equivalent (Prometheus):

```
expr: cpu_usage > 80
for: 60s
labels:
  severity: critical
  service: payment-service
```

---

## 3. Labels / Tags (Production Feature)

Real monitoring systems attach metadata to metrics and alerts.

Example labels:

```
service=order-service
env=production
region=ap-south-1
team=backend
```

Why important:

* route alerts to correct team
* filter dashboards
* group alerts
* support multi-service environments

Example:

CPU high in staging environment should not wake on-call engineer.

Schema improvement:

```
labels JSONB
```

Example:

```
{
 "service": "payment-service",
 "env": "prod",
 "team": "backend"
}
```

---

## 4. Duration Logic (Avoid False Positives)

Temporary spikes should not create alerts.

Example:

CPU spike for 2 seconds should be ignored.
CPU high for 60 seconds should trigger alert.

Your implementation tracks when violation started and only triggers alert after required duration.

Production systems rely heavily on duration windows to reduce noise.

Example:

```
avg(cpu_usage over last 5 minutes) > 80
```

Prometheus example:

```
avg_over_time(cpu_usage[5m]) > 80
```

---

## 5. Cooldown Period (Prevent Alert Spam)

Problem:

If metric fluctuates around threshold:

80 → 79 → 81 → 78 → 82

Without cooldown, multiple alerts may be triggered.

Production solution:

cooldown window prevents repeated alerts.

Example:

```
cooldown_seconds = 300
```

Meaning:

After alert resolves, system waits 5 minutes before triggering again.

---

## 6. Duplicate Alert Prevention

### Current Implementation

Uses in-memory Set:

```
activeAlerts = Set<String>
```

Purpose:

prevent repeated alerts for same problem.

Limitation:

* lost on service restart
* not shared across instances

---

### Production Approaches

Production systems prevent duplicates using persistent or distributed coordination.

Common techniques:

### fingerprinting

unique key per alert condition:

```
fingerprint = hash(service + metric + threshold + labels)
```

Example:

```
payment-service_cpu_usage_gt_80_prod
```

### database state tracking

alert stored with ACTIVE status.

### distributed cache

Redis stores active alert fingerprints.

### alert grouping

multiple alerts grouped into single incident.

---

## 7. Alert Lifecycle

### Simplified lifecycle

ACTIVE → RESOLVED

---

### Production lifecycle

PENDING → FIRING → ACKNOWLEDGED → RESOLVED → CLOSED

Meaning:

| state        | description                                 |
| ------------ | ------------------------------------------- |
| PENDING      | violation detected but waiting for duration |
| FIRING       | alert triggered                             |
| ACKNOWLEDGED | engineer is working on it                   |
| RESOLVED     | system recovered                            |
| CLOSED       | incident reviewed                           |

Additional fields:

```
acknowledged_by
acknowledged_at
resolution_time
```

---

## 8. Runbooks

Runbooks contain predefined steps to resolve alerts.

Example runbook:

High CPU Fix

1. check running processes
2. check recent deployment
3. restart service
4. analyze database queries

Benefits:

* reduces downtime
* helps junior engineers resolve incidents
* standardizes debugging
* reduces repeated troubleshooting effort

Production runbooks stored in:

* Confluence
* Notion
* Git repos
* Incident management tools

---

## 9. On-call Assignment

On-call engineers respond to alerts.

Example:

Service: Payment Service
On-call engineer: [backend-team@company.com](mailto:backend-team@company.com)

Production integrations:

* PagerDuty
* Opsgenie
* Slack
* Email
* SMS

---

## 10. On-call Rotation Scheduling (Production Feature)

Engineers rotate on-call responsibility.

Example schedule:

Week 1 → Engineer A
Week 2 → Engineer B
Week 3 → Engineer C

Production tools automatically rotate schedules.

Example schema:

```
on_call_schedule

team_name
engineer_email
start_time
end_time
priority_level
```

---

## 11. Escalation Policy

If alert is not acknowledged, system escalates.

Example:

if not acknowledged in 5 min → notify senior engineer
if not acknowledged in 15 min → notify manager

Example fields:

```
escalation_level
escalation_delay_seconds
```

---

## 12. Scheduler (Polling vs Streaming)

### Current implementation

Spring scheduler runs periodically.

Example:

```
@Scheduled(fixedRate = 15000)
```

meaning:

system checks rules every 15 seconds.

---

### Production systems use

polling model:

Prometheus scrapes metrics every 15 seconds.

push model:

applications push metrics.

streaming model:

Kafka streams metrics for real-time evaluation.

Typical intervals:

| metric type      | interval  |
| ---------------- | --------- |
| CPU usage        | 15–30 sec |
| memory           | 30 sec    |
| HTTP latency     | 30 sec    |
| business metrics | 5 min     |

Your 15 second scheduler is realistic.

---

## 13. Multi-condition Alerts (Composite Rules)

Production systems allow combining multiple conditions.

Example:

```
cpu_usage > 80
AND
error_rate > 5
FOR 2 minutes
```

Benefits:

reduces false positives.

Example:

CPU high but no errors may not require alert.

---

## 14. Maintenance Windows / Silencing

Alerts may be temporarily disabled during deployments.

Example:

Sunday 2AM–3AM deployment window.

Alerts should not trigger.

Example schema:

```
silence

start_time
end_time
scope
reason
```

---

## 15. Notification Retry Strategy

Notification systems may fail temporarily.

Example:

Slack API timeout.

Production systems retry with exponential backoff.

Example:

retry attempts:

1st retry → 30 sec
2nd retry → 60 sec
3rd retry → 120 sec

---

## 16. Observability of Observability

Production teams monitor the monitoring system.

Example alerts:

scheduler not running
metrics not received
alert service down

Ensures monitoring system is reliable.

---

# Database Schema

## Runbook

Stores resolution steps.

Fields:

* id
* title
* steps

---

## AlertRule

Defines monitoring conditions.

Fields:

* id
* name
* metric_name
* threshold
* condition
* duration_seconds
* severity
* cooldown_seconds
* labels
* runbook_id

---

## Alert

Represents triggered incidents.

Fields:

* id
* alert_rule_id
* fingerprint
* metric_value
* status
* triggered_at
* acknowledged_at
* resolved_at

---

## OnCallSchedule

Stores on-call rotations.

Fields:

* id
* team_name
* engineer_email
* start_time
* end_time

---

# Alert Evaluation Flow

Scheduler runs every fixed interval.

Steps:

1. fetch alert rules
2. fetch metric values
3. check rule condition
4. track violation start time
5. check duration requirement
6. check cooldown window
7. generate fingerprint
8. check duplicate alerts
9. create alert
10. route notification
11. resolve alert when metric normal

---

# Production Style Architecture

Application → Micrometer → Prometheus → AlertManager → PagerDuty → Engineer

---

# Architecture Diagram (ASCII)

```
                +----------------------+
                | Metrics Source       |
                |----------------------|
                | Micrometer           |
                | JVM metrics          |
                | HTTP metrics         |
                +----------+-----------+
                           |
                           v
                +----------------------+
                | Prometheus / Agent   |
                |----------------------|
                | scrape metrics       |
                +----------+-----------+
                           |
                           v
                +----------------------+
                | Alert Engine         |
                |----------------------|
                | evaluate rules       |
                | cooldown check       |
                | deduplication        |
                +----------+-----------+
                           |
                           v
                +----------------------+
                | PostgreSQL           |
                |----------------------|
                | alert rules          |
                | runbooks             |
                | alerts               |
                +----------+-----------+
                           |
                           v
                +----------------------+
                | Notification Layer   |
                |----------------------|
                | email                |
                | slack                |
                | pagerduty            |
                +----------+-----------+
                           |
                           v
                +----------------------+
                | On-call Engineer     |
                +----------------------+
```

---

# Sequence Diagram

```
Scheduler        AlertService        DB           Notification
    |                 |               |                 |
    |---trigger------>|               |                 |
    |                 |--get rules--->|                 |
    |                 |<--rules-------|                 |
    |                 | evaluate metric                |
    |                 | check duration                 |
    |                 | check cooldown                 |
    |                 | generate fingerprint           |
    |                 |--store alert-->|               |
    |                 |<--saved--------|               |
    |                 |----notify--------------------->|
    |                 |                                |
```

---

# Sample Test Data SQL

## Insert Runbooks

```sql
insert into runbook (id, title, steps) values
(1, 'High CPU Fix',
'1. Check running processes
2. Restart service
3. Check DB queries
4. Check recent deployment'),

(2, 'High Memory Fix',
'1. Check memory leak
2. Restart service
3. Increase heap size
4. Check caching logic'),

(3, 'High Error Rate Fix',
'1. Check logs
2. Check DB connection
3. Check external API
4. Rollback deployment');
```

---

## Insert Alert Rules

```sql
insert into alert_rule
(name, metric_name, threshold, condition, severity, duration_seconds, cooldown_seconds, runbook_id)
values

('High CPU','cpu_usage',80,'GT','CRITICAL',60,300,1),

('High Memory','memory_usage',75,'GT','WARNING',120,300,2),

('High Error Rate','error_rate',5,'GT','CRITICAL',30,120,3);
```

---

# Key Learning Outcomes

After building this project you understand:

* how monitoring systems detect failures
* how alerts avoid false positives
* how duplicate alerts are prevented
* how engineers respond to incidents
* how production systems reduce alert noise
* how on-call systems operate
* how observability improves reliability

---

# Possible Extensions

Future improvements:

real metrics using Spring Boot Actuator

Redis for distributed deduplication

Slack notification integration

UI dashboard

Grafana integration

Kafka streaming metrics

incident timeline tracking

postmortem tracking

---

# Conclusion

This project demonstrates real-world design patterns used in modern observability platforms.

Although simplified, the architecture closely matches production monitoring systems used by engineering teams to maintain reliability and uptime.

This project helps backend engineers understand how large-scale systems detect failures early and respond effectively.

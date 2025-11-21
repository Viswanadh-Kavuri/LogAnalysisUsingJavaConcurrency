# LogAnalysisUsingJavaConcurrencyPatterns

This repository is a playground for experimenting with **Java concurrency patterns** using a realistic-ish scenario:

> Processing large HTTP access logs to compute metrics like **global max response time**, **requests per user**, and **max per endpoint**.

The goal is not just to “make it work”, but to **compare different concurrency designs** (ExecutorService, streaming producer/consumer, map-reduce style aggregation, Disruptor, Akka, etc.) and feel their trade-offs.

---

## 1. Problem Statement

Given a large log file with lines like:

```text
2025-11-20T01:00:01Z,u2,/cart,315
2025-11-20T01:00:02Z,u1,/home,120
2025-11-20T01:00:03Z,u3,/search,980
```
We want to compute:

- Global max response time
- Per-user request count (how many requests each user made)
- Per-endpoint max response time (peak latency per API/endpoint)

And we want to do this efficiently and concurrently for millions or even hundreds of millions of log lines.

## 2.Concurrency Tools Used (So Far)

Across the ExecutorService examples, the repo demonstrates:

- ExecutorService / Executors.newFixedThreadPool(...)
- ArrayBlockingQueue as a bounded work queue
- AtomicInteger / AtomicLong and accumulateAndGet(...)
- ConcurrentHashMap with:
  - merge(key, value, remappingFn)
  - compute(key, remappingFn)
- CountDownLatch vs awaitTermination vs Thread.join
- “Poison pill” shutdown pattern "__END__"


## 3.Next Steps (Planned / In Progress)

This repo is also a playground for exploring other concurrency frameworks using the same log analysis scenario:

#### ForkJoinPool
- Recursive partitioning of the log file into chunks, work-stealing, map-reduce style merge.

#### LMAX Disruptor
- Replace ArrayBlockingQueue with a ring buffer for lower latency and fewer allocations, with event handlers for parsing and aggregation.

#### Akka (Actors)
Actor model where:

- Producer actor reads logs.
- Aggregator actors maintain per-user / per-endpoint stats.
- Messages are passed asynchronously, making it easier to scale out and add resilience.

Each version will reuse the same core metrics (global max, per-user counts, per-endpoint max) so you can compare designs directly.

#!/usr/bin/env bash
set -euo pipefail

KUBERNETES_NAMESPACE=$(oc project -q)
echo "⚙️ Using namespace: $KUBERNETES_NAMESPACE"

# Build & deploy with Quarkus Kubernetes extension
mvn clean package \
  -Dquarkus.kubernetes.deploy=true \
  -Dquarkus.kubernetes.namespace=${KUBERNETES_NAMESPACE} \
  -Dquarkus.container-image.group=${KUBERNETES_NAMESPACE} \
  2>&1 | tee build.log

# Apply any extra OpenShift resources
if [ -d src/main/k8s ]; then
  echo "📦 Applying k8s resources from src/main/k8s..."
  oc apply -f src/main/k8s/ -n ${KUBERNETES_NAMESPACE}
fi


clear

echo "✅ Deployment completed to namespace: ${KUBERNETES_NAMESPACE}"
echo "🌀 Kafka WEB URL : https://kafka-ui-workshop-kafka.apps.<cluster-domain>"

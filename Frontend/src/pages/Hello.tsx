import { Button } from "@/components/ui/button";
import { toast } from "react-toastify";

// src/pages/Hello.tsx
export default function Hello() {
const notify = () => toast('Wow so easy !');
  return (
    <div className="p-6">
      <h1 className="text-3xl font-bold">Hello World</h1>
      <Button className="bg-red-300" onClick={notify}>Notify !</Button>
    </div>
  );
}
